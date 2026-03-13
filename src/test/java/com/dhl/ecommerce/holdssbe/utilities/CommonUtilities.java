package com.dhl.ecommerce.holdssbe.utilities;

import com.dhl.ecommerce.holdssbe.ConfigProvider;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Slf4j
public class CommonUtilities {
    protected WebDriver driver;
    protected ConfigProvider configProvider;

    private final Random random;

    public CommonUtilities(final WebDriver driver, final ConfigProvider configProvider) {
        this.driver = driver;
        this.configProvider =  configProvider;
        this.random = new Random();
    }

    // Return the List of Maps after merging 2 different List of Maps
    public List<Map<String, String>> mergeListOfMaps(final List<Map<String, String>> list1, final List<Map<String, String>> list2) {
        final int size = list1.size();
        final List<Map<String, String>> mergedList = new ArrayList<>(size);

        for (int i = 0; i < list1.size(); i++) {
            final Map<String, String> mergedMap = new ConcurrentHashMap<>(list1.get(i));
            mergedMap.putAll(list2.get(i));
            mergedList.add(mergedMap);
        }
        return mergedList;
    }

    // To return the Random Index number with in the limit of ( 0 to maxLimit)
    public int getRandomInteger(final int maxLimit) {
        final int randomIndex = (maxLimit > 1) ? random.nextInt(maxLimit - 1) + 1 : 0;
        log.info("Max Limit : '{}' & Random Index : '{}'",maxLimit,randomIndex);
        return randomIndex;
    }

    /**
     * Retrieves the Salesforce Case ID for a given case number displayed on the UI.
     * This method checks the dashboard type (mobile or other) and constructs an appropriate XPath to locate
     * the case element on the UI. It extracts the Salesforce Case ID by either:
     * - For mobile: Extracting the Case ID from the `data-testid` attribute of the corresponding element.
     * - For other dashboard types: Retrieving the Case ID from the `data-id` attribute of the element associated with the case number.
     * The method is case-insensitive when matching the dashboard type and trims any extra spaces from the case number.
     *
     * @param caseNumber    The case number whose Salesforce Case ID is to be retrieved.
     * @param dashboardType The type of dashboard (e.g., "mobile" or "desktop") to determine the method of extraction.
     * @return The Salesforce Case ID as a String, or an empty string if not found.
     */
    public String getSFCaseID(final String caseNumber, final String dashboardType) {
        final WebElement element;
        final String caseID;

        if (dashboardType.toLowerCase(Locale.ROOT).contains("mobile")) {
            element = driver.findElement(By.xpath(String.format("//div[contains(@data-testid, 'ph-card-openHoldDetail')][.//p[text()='%s']]", caseNumber.trim())));
            final String dataTestIDValue = element.getAttribute("data-testid");
            final int lastIndex = dataTestIDValue.lastIndexOf('-'); // Find the last index of "-"
            caseID = dataTestIDValue.substring(lastIndex + 1); // Extract the substring after the last "-"
        } else {
            element = driver.findElement(By.xpath(String.format("//*[contains(text(),'%s')]/ancestor::div[@data-id]", caseNumber.trim())));
            caseID = element.getAttribute("data-id");
        }

        return caseID;
    }

    public List<String> getListBySplitString(final String fieldList, final String splitString) {
        return new ArrayList<>(Arrays.asList(fieldList.split(splitString)));
    }

    public boolean compareKeyValuePairInMaps(final Map<String, String> expectedMap,final Map<String, String> actualMap) {
        // Iterate through the keys in the expected map
        boolean status = true;
        for (final String key : expectedMap.keySet()) {
            // Check if the key exists in both maps
            if (actualMap.containsKey(key)) {
                final String expectedValue = expectedMap.get(key);
                final String actualValue = actualMap.get(key);
                if (!expectedValue.equals(actualValue)) {
                    log.info(key + " does not match. Expected: " + expectedValue + ", Actual: " + actualValue);
                    status = false;
                }
            }
        }
        return status;
    }

    public String dateFormatMMDD(final String inputDate) {
        final DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        final ZonedDateTime dateTime = ZonedDateTime.parse(inputDate, inputFormatter);
        final DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("EEEE - MM/dd", Locale.ENGLISH);
        return dateTime.format(outputFormatter);
    }
}
