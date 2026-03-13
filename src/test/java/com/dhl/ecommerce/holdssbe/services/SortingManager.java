package com.dhl.ecommerce.holdssbe.services;

import com.dhl.ecommerce.holdssbe.ConfigProvider;
import com.dhl.ecommerce.holdssbe.pages.GenericPage;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
public class SortingManager {
    protected WebDriver driver;
    protected ConfigProvider configProvider;
    protected GenericPage genericElement;

    private static final String ASCENDING_ORDER = "ascending";
    private static final String DESCENDING_ORDER = "descending";

    public SortingManager(final WebDriver driver, final ConfigProvider configPro) {
        this.driver = driver;
        this.configProvider = configPro;
        genericElement= new GenericPage(driver,configProvider);
    }

    /**
     * Sorts the records based on the specified column name and order.
     * This method finds the column header in the table using the provided column name,
     * and sorts the records in either ascending or descending order based on the
     * specified order. For ascending order, a single click on the column header is performed,
     * while for descending order, a double-click is executed.
     *
     * @param columnName The name of the column to sort by (e.g., "CASE_NUMBER", "Status").
     * @param order The order in which to sort the records. Valid values are "ascending" and "descending".
     */
    public void sortRecordsByColumn(final String columnName, final String order) {
        final WebElement ele = genericElement.getElementByRegText(columnName);
        if (ASCENDING_ORDER.equals(order)) {
            ele.click();
        } else if (DESCENDING_ORDER.equals(order)) {
            final Actions actions = new Actions(driver);
            actions.doubleClick(ele).perform();
        }
    }

    /**
     * Retrieves a list of values from the records, sorted by the specified column.
     * This method fetches the hold case records from the dashboard and collects
     * the values from the column specified by the `sortBy` parameter.
     * It returns a list of values for the field corresponding to the provided column name.
     *
     * @param sortBy The name of the column (field) by which to sort the records and retrieve the values.
     *               The method assumes that the column name corresponds to a valid key in the records.
     *
     * @return A List of Strings containing the values from the specified column for each record.
     */
    public List<String> getSortedValueFromRecord(final List<Map<String, String>> records , final String sortBy) {
        final List<String> sortByDetails = new ArrayList<>();
        for (final Map<String, String> record : records) {
            sortByDetails.add(record.get(sortBy));
        }
        return sortByDetails;
    }

    /**
     * Retrieves a list of values from the hold case records displayed on a mobile interface,
     * sorted by the specified column name.
     * This method fetches the hold case records displayed on a mobile device and collects
     * the values from the column specified by the `sortBy` parameter.
     * It returns a list of values corresponding to the provided column name for each record.
     *
     * @param sortBy The name of the column (field) by which to sort the records and retrieve the values.
     *               The column name corresponds to a valid key in the records.
     *
     * @return A List of Strings containing the values from the specified column for each record
     *         displayed on the mobile interface.
     */
    public List<String> getSortedValueFromRecordInMobile(final List<Map<String, String>> records, final String sortBy) {
        final List<String> sortByDetails = new ArrayList<>();
        for (final Map<String, String> record : records) {
            sortByDetails.add(record.get(sortBy));
        }
        return sortByDetails;
    }

}