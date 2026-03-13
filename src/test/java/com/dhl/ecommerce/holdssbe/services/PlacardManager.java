package com.dhl.ecommerce.holdssbe.services;

import com.dhl.ecommerce.holdssbe.ConfigProvider;
import com.dhl.ecommerce.holdssbe.Constants;
import com.dhl.ecommerce.holdssbe.SharedContext;
import com.dhl.ecommerce.holdssbe.utilities.FileUtilities;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.util.AssertionFailedException;
import org.openqa.selenium.WebDriver;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.IntStream;

@Slf4j
public class PlacardManager {

    protected WebDriver driver;
    protected ConfigProvider configProvider;

    private static final String HOLD_STATUS = "HOLD";
    private static final int DESC_PLACARD = 500;
    private static final String DESCRIPTION = "Description";
    private static final String BOL_DSM = "BOL/DSM Number";
    private static final String CUSTOMER_PICKUP_NUMBER = "CUSTOMER NUMBER / PICKUP NUMBER";
    private static final String TOTAL_PALLETS = "Total Number of Pallets";
    private static final String MAX_PC_CNT = "MAX PC CNT";
    private static final String EXP_PC_CNT = "EXP PC CNT";
    private static final String GND_PC_CNT = "GND PC CNT";
    private static final String INTL_PC_CNT = "INTL PC CNT";
    private static final String PICKUP_NAME = "Pickup Customer Name";
    private static final String MAX = "MAX";
    private static final String EXP = "EXP";
    private static final String GND = "GND";
    private static final String INTL = "INTL";

    public PlacardManager(final WebDriver driver, final ConfigProvider configPro) {
        this.driver = driver;
        this.configProvider = configPro;
    }

    public String getPlacardContent(final String directoryPath, final String fileName) {
        try {
            Thread.sleep(Integer.parseInt(configProvider.getProperty(Constants.SELENIUM_LONG_WAIT)) * 1000L);
        } catch (InterruptedException e) {
            log.debug("Not able to perform wait perform :{}", e.getMessage());
        }

        final FileUtilities fileUtilities = new FileUtilities(driver, configProvider);
        return fileUtilities.getPDFFileContent(directoryPath, fileName);
    }


    public Map<String, String> getPlacardDetails(final String directoryPath, final String fileName) {
        final String pdfContent = getPlacardContent(directoryPath, fileName);
        log.debug("pdfContent: \n" + pdfContent);

        final List<String> plaLines = Arrays.asList(pdfContent.split("\n"));

        for (int i = 0; i < plaLines.size(); i++) {
            log.debug("Line: {} - {}", i, plaLines.get(i));
        }

        final Map<String, String> placardValues = new ConcurrentHashMap<>();

        placardValues.put("Date MM/DD", plaLines.get(0).trim());
        placardValues.put("Is Hold", plaLines.get(1).trim());
        placardValues.put("Type", plaLines.get(2).trim());

        placardValues.put(DESCRIPTION, getValueFromPlacard(plaLines, DESCRIPTION.toUpperCase(Locale.ROOT)));

        addServiceFromPlacard(placardValues, plaLines);

        placardValues.put("Case Number", getValueFromPlacard(plaLines, "SALESFORCE CASE NUMBER"));
        placardValues.put(BOL_DSM, getValueFromPlacard(plaLines, BOL_DSM.toUpperCase(Locale.ROOT)));

        final String actPickUpCustomerNo = getValueFromPlacard(plaLines, CUSTOMER_PICKUP_NUMBER);
        final int lastSpaceIndex = actPickUpCustomerNo.lastIndexOf(" ");
        placardValues.put(CUSTOMER_PICKUP_NUMBER, actPickUpCustomerNo.substring(lastSpaceIndex + 1).trim());
        placardValues.put(PICKUP_NAME, getValueFromPlacard(plaLines, PICKUP_NAME.toUpperCase(Locale.ROOT)));

        placardValues.put(TOTAL_PALLETS, getValueFromPlacard(plaLines, "NUMBER OF PALLETS"));
        placardValues.put("Created Date", getValueFromPlacard(plaLines, "CREATED DATE"));
        placardValues.put("Created By", getValueFromPlacard(plaLines, "PLACED ON HOLD BY"));

        placardValues.put("Facility", plaLines.get(plaLines.size() - 3).replace("FACILITY", "").trim());

        return placardValues;
    }

    public String getValueFromPlacard(final List<String> lines, final String fieldName) {
        final int fieldIndex = fieldName.equals(DESCRIPTION.toUpperCase(Locale.ROOT)) ? getLineIndex(lines, HOLD_STATUS)+2 : getLineIndex(lines, fieldName);
        final int nextFieldIndex = fieldName.equals(DESCRIPTION.toUpperCase(Locale.ROOT)) ? getLineIndex(lines, MAX) : getLineIndex(lines, getNextField(fieldName));

        final StringBuilder result = new StringBuilder();
        for (int i = fieldIndex ; i < nextFieldIndex; i++) {
            final String line = lines.get(i).trim();
            result.append(line);
        }
        return result.toString().replace(fieldName,"").trim();
    }

    private int getLineIndex(final List<String> lines,final String fieldName) {
        return IntStream.range(0, lines.size())
                .filter(i -> lines.get(i).startsWith(fieldName))
                .findFirst()
                .orElseThrow(() -> new AssertionFailedException(fieldName + " is not found in the PDF Contents."));
    }


    private String getNextField(final String currentField) {
        final String[] placardFieldsSeq = Constants.PLACARD_FIELDS_SEQ;
        final int index = Arrays.asList(placardFieldsSeq).indexOf(currentField.trim());
        return (index >= 0 && index < placardFieldsSeq.length - 1) ? placardFieldsSeq[index + 1] : placardFieldsSeq[placardFieldsSeq.length -1];
    }

    private void addServiceFromPlacard(final Map<String, String> placardValues,final List<String> plaLines) {
        final int maxIndex = getLineIndex(plaLines, MAX);

        final String[] serviceNames = plaLines.get(maxIndex).trim().split("\\s+");
        final String[] serviceValues = plaLines.get(maxIndex + 1).trim().split("\\s+");

        for (int i = 0; i < serviceNames.length; i++) {
            placardValues.put(serviceNames[i], serviceValues[i]);
        }
    }

    public Map<String, String> getExpectedValueForPlacardFromHoldCreate() {
        final String selectedPickup= SharedContext.getInstance().getSelectedPickup();
        final String selectedHoldType=SharedContext.getInstance().getSelectedHoldType();
        final Map<String, String> selectedFieldsValues=SharedContext.getInstance().getSelectedFieldsValues();
        final String newCaseNumber=SharedContext.getInstance().getNewCaseNumber();

        final Map<String, String> data = new ConcurrentHashMap<>();
//      data.put("Date MM/DD", plaLines.get(0).trim());
        data.put("Is Hold", HOLD_STATUS);
        data.put("Type", selectedHoldType);
        final String actualDescription = selectedFieldsValues.get(DESCRIPTION);
        if (actualDescription.length() > DESC_PLACARD) {
            data.put(DESCRIPTION, actualDescription.substring(0, DESC_PLACARD) + "...");
        } else {
            data.put(DESCRIPTION, selectedFieldsValues.get(DESCRIPTION));
        }
        data.put("Case Number", newCaseNumber);
        data.put(BOL_DSM, selectedFieldsValues.get(BOL_DSM));
        data.put(CUSTOMER_PICKUP_NUMBER, selectedPickup.split("\n")[1]);
        data.put(PICKUP_NAME, selectedPickup.split("\n")[0]);
        data.put(TOTAL_PALLETS, selectedFieldsValues.get(TOTAL_PALLETS));
        data.put(MAX, selectedFieldsValues.get(MAX_PC_CNT));
        data.put(EXP, selectedFieldsValues.get(EXP_PC_CNT));
        data.put(GND, selectedFieldsValues.get(GND_PC_CNT));
        data.put(INTL, selectedFieldsValues.get(INTL_PC_CNT));
        return data;
    }

}
