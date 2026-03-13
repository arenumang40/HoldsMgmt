package com.dhl.ecommerce.holdssbe.services;

import com.dhl.ecommerce.holdssbe.ConfigProvider;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class BGColorManager {

    private static final String CASE_STATUS = "status";
    private static final String BG_COLOR = "background-color";

    protected WebDriver driver;
    protected ConfigProvider configProvider;

    public BGColorManager(final WebDriver driver, final ConfigProvider configPro) {
        this.driver = driver;
        this.configProvider = configPro;
    }

    /**
     * Retrieves the status and background color for each hold case row.
     * Returns a list of maps with "status" and "BG_COLOR".
     */
    public List<Map<String, String>> getActualStatusBgColorList(final List<WebElement> holdCaseRows) {
        final List<Map<String, String>> actualBgColorList = new ArrayList<>();

        for (final WebElement holdCase : holdCaseRows) {
            final Map<String, String> map = new ConcurrentHashMap<>();
            final WebElement statusCell = holdCase.findElement(By.xpath(".//div[@data-field='status']"));
            final String statusText = statusCell.getText();

            map.put(CASE_STATUS, statusText);
            map.put(BG_COLOR, getBgColorElement(holdCase));

            actualBgColorList.add(map);
        }
        return actualBgColorList;
    }

    /**
     * Gets the background color of the `::before` pseudo-element of an element.
     * Returns the color in RGB format or an empty string if not set.
     */
     public String getBgColorElement(final WebElement ele) {
        return (String) ((JavascriptExecutor) driver).executeScript("return window.getComputedStyle(arguments[0], '::before').getPropertyValue('background-color');", ele);
    }

    /**
     * Retrieves the status counts and background colors from mobile status tiles.
     * Returns a list of maps where each map contains:
     * - "status_caseCount": The case count from the status tile.
     * - "BG_COLOR": The background color of the status tile.
     */
    public List<Map<String, String>> getActualStatusMobileBgColorList(final List<WebElement> mobileStatusTiles) {
        final List<Map<String, String>> actualBgColor = new ArrayList<>();

        for (final WebElement holdCaseStatus : mobileStatusTiles) {
            final Map<String, String> map = new ConcurrentHashMap<>();

            map.put("status_caseCount", holdCaseStatus.getText().split("[,\\n]")[0].trim());
            map.put(BG_COLOR, holdCaseStatus.getCssValue(BG_COLOR));

            actualBgColor.add(map);
        }

        log.info("List of Actual Status Count & BG : '{}'", actualBgColor);
        return actualBgColor;
    }

    /**
     * Verifies if the background color of each record in the actual list matches the expected status color.
     * @param expected The expected records with "status" and "BG_COLOR".
     * @param actual   The actual records to compare.
     * @return `true` if all match, `false` if any mismatch.
     */
    public boolean isBackgroundColorMatchForEachRecord(final List<Map<String, String>> expected, final List<Map<String, String>> actual) {
        boolean isAllMatch = true;

        for (final Map<String, String> actualItem : actual) {
            final String actualStatus = actualItem.get(CASE_STATUS);
            final String actualBgColor = actualItem.get(BG_COLOR);

            final boolean isMatchForRecord = checkIfBackgroundColorMatches(expected, actualStatus, actualBgColor);

            if (!isMatchForRecord) {
                isAllMatch = false; // If any record doesn't match, mark as false
            }
        }
        logResults(isAllMatch);
        return isAllMatch;
    }

    private void logResults(final boolean isAllMatch) {
        if (isAllMatch) {
            log.info("All Records are matched");
        } else {
            log.error("Few Records are mismatched");
        }
    }

    /**
     * Compares the background color of a status with the expected value.
     * Logs messages for matches, mismatches, or missing status in the expected list.
     * @param expected The list of expected records with "status" and "BG_COLOR".
     * @param actualStatus The actual status to check.
     * @param actualBgColor The actual background color to check.
     * @return true if the background color matches, false otherwise.
     */
    private boolean checkIfBackgroundColorMatches(final List<Map<String, String>> expected, final String actualStatus, final String actualBgColor) {
        boolean isMatch = false;

        for (final Map<String, String> expectedItem : expected) {
            final String expectedStatus = expectedItem.get(CASE_STATUS);
            final String expectedBgColor = expectedItem.get(BG_COLOR);

            if (actualStatus.equals(expectedStatus)) {
                if (actualBgColor.equals(expectedBgColor)) {
                    log.info("Background color matched for status '{}': Expected = '{}' , Actual = '{}'", actualStatus, expectedBgColor, actualBgColor);
                    isMatch = true;
                } else {
                    log.error("Background color mismatch for status '{}': Expected = '{}' , Actual = '{}'", actualStatus, expectedBgColor, actualBgColor);
                    isMatch = false;
                }
            }
        }
        if (!isMatch) {
            log.error("Status '{}' not found in expected.", actualStatus);
        }
        return isMatch;
    }

}