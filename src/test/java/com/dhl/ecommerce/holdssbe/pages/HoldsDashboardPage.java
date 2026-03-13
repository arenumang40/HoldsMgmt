package com.dhl.ecommerce.holdssbe.pages;

import com.dhl.ecommerce.holdssbe.ConfigProvider;
import com.dhl.ecommerce.holdssbe.Constants;
import com.dhl.ecommerce.holdssbe.api.APIClient;
import com.dhl.ecommerce.holdssbe.api.APIClientFactory;
import com.dhl.ecommerce.holdssbe.services.SalesforceManager;
import com.dhl.ecommerce.holdssbe.services.SortingManager;
import com.dhl.ecommerce.holdssbe.services.TestdataProcessor;
import com.dhl.ecommerce.holdssbe.utilities.CommonUtilities;
import com.dhl.ecommerce.holdssbe.utilities.ScrollUtilities;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import static org.testng.FileAssert.fail;

@Slf4j
public class HoldsDashboardPage extends ApplicationPage {
    private static final String COMMA_SPACE_REGEX = ",\\s*";

    @FindBy(xpath = "//*[@role='columnheader']")
    List<WebElement> gridHeaderList;

    @FindBy(xpath = "//*[@role='gridcell']")
    List<WebElement> gridCellRecords;

    @FindBy(xpath = "//div[contains(@class,'MuiDataGrid-toolbarContainer')]/button")
    List<WebElement> toolbarOptions;

    @FindBy(xpath = "//div[contains(@class,'MuiBox-root css-1c1kq07')]/span")
    List<WebElement> footerServices;

    @Getter
    @FindBy(xpath = "//*[contains(@class,'MuiDataGrid-scrollbar--horizontal')]")
    List<WebElement> horizontalScrollbars;

    @Getter
    @FindBy(xpath = "//div[contains(@class, 'MuiDataGrid-main')]")
    WebElement verticalScrollbar;

    @FindBy(xpath = "//*[@role='rowgroup']/div")
    List<WebElement> holdsRecordList;

    @Getter
    @FindBy(xpath = "//*[contains(@data-testid,'ph-btn-expandStatus')]/div")
    List<WebElement> mobileStatusTiles;

    @Getter
    @FindBy(xpath = "//div[@data-field='caseNumber']")
    List<WebElement> caseNumberList;

    @Getter
    @FindBy(xpath = "//span[contains(text(), 'Case Number')]/following-sibling::p")
    List<WebElement> caseTileInMobile;

    @FindBy(xpath = "//*[contains(@data-testid,'ph-card-openHoldDetail')]")
    List<WebElement> expandedMobTiles;

    @FindBy(xpath = "//label[contains(@class, 'MuiFormControlLabel-root')]")
    List<WebElement> colWithCheckbox ;

    @Getter
    @FindBy(xpath = "//*[@role='rowgroup']/div")
    List<WebElement> holdCaseRows;

    @FindBy(xpath = "//*[contains(@data-testid,'ph-card-openHoldDetail')]/div")
    List<WebElement> openedHold;

    @FindBy(xpath = "//p[text()='Total rows']/following-sibling::p")
    WebElement rowCount;

    public HoldsDashboardPage(final WebDriver driver, final ConfigProvider configProvider) {
        super(driver,configProvider);
        this.url = "/holds";
        this.title =  "HOLDS MANAGEMENT";
        this.name = "Holds Dashboard";
    }

    public List<WebElement> holdsElementDisplayOnDashboard(final String dashboardType) {
        return dashboardType.toLowerCase(Locale.ROOT).contains("mobile") ? getCaseTileInMobile() : getCaseNumberList();
    }

    /**
     * Compares actual and expected records,
     * updates mismatched cases, and triggers close operation for missing records.
     *
     * @param actualList A list of actual data records.
     * @param expectedList A list of expected data records.
     */
    public void processTestData(final List<Map<String, String>> actualList, final List<Map<String, String>> expectedList) {
        final APIClientFactory apiClientFactory = new APIClientFactory();
        final APIClient apiClient = apiClientFactory.generateAPIClient(configProvider.getProperty(Constants.API_ENVIRONMENT));
        final TestdataProcessor tdp = new TestdataProcessor(driver,configProvider);

        for (final Map<String, String> actualRecord : actualList) {
            tdp.processActualRecord(actualRecord, expectedList, apiClient);
        }
        reloadPage();
    }

    /**
     * Retrieves the header labels of a table grid.
     * This method iterates over each header element in the grid and collects its
     * "aria-label" attribute, which is assumed to represent the header label.
     * The list of headers is then returned as a list of strings.
     *
     * @return A List of Strings containing the headers of the grid.
     */
    public List<String> getGridHeaderList() {
        final List<String> actualHeaderList = new ArrayList<>();
        for (final WebElement header : gridHeaderList) {
            actualHeaderList.add(header.getAttribute("aria-label").trim());
        }
        return actualHeaderList;
    }

    /**
     * Retrieves the list of all Hold Cases currently displayed on the dashboard.
     * This method assumes that the grid structure consists of rows and columns, where
     * each row represents a record and each column corresponds to a particular attribute
     * (such as Case Number, Case Status, etc.). It collects the displayed cell values and
     * returns them as a list of maps, where each map represents a record, with the column
     * headers as the keys and the respective cell values as the map values.
     *
     * @return A List of Maps, where each Map contains key-value pairs representing the
     *         header names and corresponding cell values for each record in the grid.
     */
    public List<Map<String, String>> getActualHoldCaseDisplayed() {
        scrollGridHorizontally();
        return extractRecords();
    }

    private void scrollGridHorizontally() {
        final ScrollUtilities scrollUtilities = new ScrollUtilities(driver, configProvider);
        scrollUtilities.moveHorizontalScrollbarToLeft(horizontalScrollbars);
    }

    private List<Map<String, String>> extractRecords() {
        final List<String> headers = getGridHeaderList();
        final int numberOfRecords = getHoldsCount();
        log.info("No of Records: '{}' with '{}' Headers. (Total: '{}')", numberOfRecords, headers.size(), gridCellRecords.size());

        final List<Map<String, String>> records = new ArrayList<>();
        final Map<String, String> recordMap = new ConcurrentHashMap<>();
        waitTillTimeOut(Constants.SELENIUM_IMPLICIT_WAIT);
        for (int i = 0; i < numberOfRecords; i++) {
            recordMap.clear();

            for (int j = 0; j < headers.size(); j++) {
                final int index = i * headers.size() + j;
                waitTillVisible(gridCellRecords.get(index));
                final String cellValue = gridCellRecords.get(index).getText();
                recordMap.put(headers.get(j), cellValue);
            }
            records.add(new HashMap<>(recordMap));
        }
        log.info("Actual Hold Case Records :" + records);
        return records;
    }

    public int getHoldsCount() {
        final List<String> headers = getGridHeaderList();
        int holdsCount = 0;
        if (!(headers.isEmpty() || gridCellRecords.isEmpty())) {
            log.debug("Make sure, Page get fully loaded.");
            holdsCount = gridCellRecords.size() / headers.size();
        }
        return holdsCount;
    }

    // To Return the List of all Hold Case after Removing 'Time on Hold' if exist
    public List<Map<String, String>> removeTimeOnHold(final List<Map<String, String>> records) {
        final List<Map<String, String>> mutableRecords = new ArrayList<>();
        for (final Map<String, String> record : records) {
            final Map<String, String> mutableRecord = new ConcurrentHashMap<>(record);
            // Remove the "Time on Hold" key if it exists
            mutableRecord.remove("Time on Hold");
            mutableRecords.add(mutableRecord);
        }
        return mutableRecords;
    }

    /**
     * Retrieves a list of options from the dashboard toolbar.
     * This method iterates over a list of toolbar elements, extracts their inner text,
     * removes any occurrences of the digit "0" (if present), and trims any extra whitespace.
     * The resulting list of clean option names (e.g., "Columns", "Filters", "Density", "Exports") is returned.
     *
     * @return List<String> A list of strings representing the cleaned toolbar options.
     */
    public List<String> getDashboardToolbarOptionList() {
        final List<String> actualList = new ArrayList<>();
        for (final WebElement toolbar : toolbarOptions) {
            final String text = toolbar.getAttribute("innerText");
            final String cleanText = text.replace("0", "").trim();
            actualList.add(cleanText);
        }
        return actualList;
    }

    // To Return the list of Services displayed at the bottom with Values
    public Map<String, String> getFooterServicesWithValue() {
             // Collect elements in pairs into a Map using Streams
        return IntStream.range(0, footerServices.size() / 2)
                .boxed()
                .collect(Collectors.toMap(
                        i -> footerServices.get(i * 2).getText(), // Key is at even indices
                        i -> footerServices.get(i * 2 + 1).getText(), // Value is at odd indices
                        (existing, replacement) -> existing,
                        LinkedHashMap::new
                ));
    }

    // To Return Only the list of Services displayed at the bottom
    public List<String> getFooterServicesList() {
        final Map<String, String> servicesValues = getFooterServicesWithValue();
        return new ArrayList<>(servicesValues.keySet());
    }

    /**
     * Retrieves a list of unique values from the records, grouped by the specified column name.
     * This method first retrieves the sorted list of values from the specified column, and then
     * iterates through the list to group the values by eliminating duplicates, ensuring that only
     * unique values are included in the returned list.
     *
     * @param columnName The name of the column whose values will be grouped. The column name
     *                   corresponds to a key in the records.
     *
     * @return A List of Strings containing unique values from the specified column, with duplicates removed.
     */
    public List<String> getGroupedFieldValueOrder(final String columnName) {
        final SortingManager sortingManager = new SortingManager(driver,configProvider);
        final List<String> fieldValues = sortingManager.getSortedValueFromRecord(getActualHoldCaseDisplayed(),columnName);
        final List<String> groupedFieldValue = new ArrayList<>();
        groupedFieldValue.add(fieldValues.get(0));
        for (int i = 1; i < fieldValues.size(); i++) {
            final int curListSize = groupedFieldValue.size();
            if (!fieldValues.get(i).equals(groupedFieldValue.get(curListSize - 1))) {
                groupedFieldValue.add(fieldValues.get(i));
            }
        }
        return groupedFieldValue;
    }

    /**
     * Calculates the total sum of the specified service values from the displayed grid.
     * This method locates all the grid cells associated with the given service and sums up their values.
     * It ignores any grid cells where the value is "—" (which likely indicates unfilled value).
     *
     * @param service The name of the service whose values need to be summed. This corresponds to the
     *                `data-field` attribute of the grid cells.
     *
     * @return The total sum of the service values. If no valid values are found, it returns 0.
     */
    public int getServiceSum(final String service) {
        int sumServices = 0;
        final List<WebElement> elements = driver.findElements(By.xpath("//*[@role='gridcell' and @data-field='" + service + "']"));
        for (final WebElement ele : elements) {
            if (!"—".equals(ele.getText())) {
                final int val = Integer.parseInt(ele.getText());
                sumServices = sumServices + val;
            }
        }
        return sumServices;
    }

    /**
     * Retrieves the list of columns along with their checkbox statuses from the UI.
     * This method iterates through all the columns that are displayed in the form or grid,
     * and for each column, it captures the column name, the enabled status of the checkbox,
     * and whether the checkbox is selected or not.
     *
     * @return A List of Maps, where each Map contains details for a column:
     *         - "columnName": The name of the column.
     *         - "isEnable": Whether the checkbox for the column is enabled (true or false).
     *         - "isSelected": Whether the checkbox for the column is selected (true or false).
     */
    public List<Map<String, String>> getActualListOfColumnsWithCheckBox() {
       final List<Map<String, String>> columnCheckBox = new ArrayList<>();

        for (final WebElement column : colWithCheckbox) {
            final Map<String, String> map = new ConcurrentHashMap<>();
            final WebElement columnElement = column.findElement(By.xpath(".//span[contains(@class, 'MuiTypography-root')]"));
            final WebElement checkboxElement = column.findElement(By.xpath(".//input"));
            final String columnName = columnElement.getText();
            final boolean isChecked = checkboxElement.isSelected();
            final boolean isEnabled = checkboxElement.isEnabled();

            map.put("columnName", columnName);
            map.put("isEnable", String.valueOf(isEnabled));
            map.put("isSelected", String.valueOf(isChecked));

            columnCheckBox.add(map);
        }

        log.info("Column Details are as follows : '{}'", columnCheckBox);
        return columnCheckBox;
    }

    /**
     * Unselects the checkbox for a specified column in the UI.
     * This method locates the checkbox element corresponding to the given column name and performs a click action
     * to unselect the checkbox (if it's selected). It logs the current selection state of the checkbox after the click.
     *
     * @param columnName The name of the column whose checkbox needs to be unselected.
     *                   The column name is used to locate the corresponding checkbox element in the UI.
     */
    public void clickOnColumnCheckbox(final String columnName) {
        // WebElement for Column Element To unSelect
        final WebElement checkbox = driver.findElement(By.xpath("//label[contains(., '" + columnName + "')]//input"));
        checkbox.click();
    }

    /**
     * Verifies that all hold cases match the expected density style.
     * This method iterates through a list of hold case elements and compares their "style" attribute
     * with the expected style. If any hold case does not match the expected style, the method returns `false`.
     * If all hold cases match the expected style, it returns `true`.
     *
     * @param expectedStyle The style value that each hold case element is expected to have.
     *                      This is compared with the "style" attribute of each element in `holdsRecordList`.
     *
     * @return `true` if all hold cases have the expected style, otherwise `false`.
     */
    public boolean isDensityStyleMatched(final String expectedStyle) {
        boolean status = true;

        for (final WebElement ele : holdsRecordList) {
            final String actualStyle = ele.getAttribute("style");
            if (!expectedStyle.equals(actualStyle)) {
                status = false;
            }
        }
        return status;
    }

    /**
     * Clicks on a status tab by its name in the list of mobile status tiles.
     * This method iterates through the list of status tiles and clicks on the one that matches the provided tab name.
     * If the tab name is found, it performs a click action and logs the event.
     *
     * @param statusTabName The name of the status tab to click.
     */
    public void clickedStatusTab(final String statusTabName) {
        for (final WebElement ele : mobileStatusTiles) {
            if (ele.getText().trim().contains(statusTabName.trim())) {
                ele.click();
                log.info("element get clicked - {}", ele.getText());
                break;
            }
        }
    }

    /**
     * Returns the count of opened hold tiles on the mobile interface.
     * This method retrieves the number of elements present in the list of expanded hold tiles on the mobile view.
     * It helps to validate how many hold tiles are currently expanded on the mobile interface.
     *
     * @return The count of opened hold tiles in the mobile interface.
     */
    public int getOpenedHoldTileCount() {
        waitTillVisible(expandedMobTiles.get(0));
        return expandedMobTiles.size();
    }

    /**
     * Retrieves the details of expanded hold cases displayed on mobile.
     * This method iterates over a list of hold tiles, extracts the header names and their corresponding values,
     * and returns a list of maps representing the hold case details. Each map contains key-value pairs where the key
     * is the header name (e.g., "CASE_NUMBER", "Status"), and the value is the corresponding field value (e.g., "12345", "Open").
     *
     * @return List<Map<String, String>> A list of maps, where each map contains header-value pairs for an individual hold case.
     */
    public List<Map<String, String>> getHoldCaseDetailDisplayedOnMobile() {
        final List<Map<String, String>> mobHoldsDisplayed = new ArrayList<>();

        for (final WebElement ele : expandedMobTiles) {

            final List<WebElement> headersName = ele.findElements(By.xpath(".//span[contains(@class,'MuiTypography-caption')]"));
            final List<WebElement> headersValue = ele.findElements(By.xpath(".//span[contains(@class,'MuiTypography-caption')]/following-sibling::p"));

            if (headersName.size() == headersValue.size()) {
                final Map<String, String> map = new ConcurrentHashMap<>();
                for (int i = 0; i < headersName.size(); i++) {
                    final String headerKey = headersName.get(i).getText().trim();
                    final String headerValue = headersValue.get(i).getText().trim();
                    map.put(headerKey, headerValue);
                }
                mobHoldsDisplayed.add(map);
            } else {
                log.error("Header and value sizes do not match for an entry.");
            }
        }
        return mobHoldsDisplayed;
    }

    /**
     * Retrieves the details of expanded hold case services displayed on mobile.
     * This method iterates over a list of elements representing hold case services and extracts key-value pairs
     * for each service. The key-value pairs represent service names and their corresponding values (e.g., "Service Name" -> "Active").
     * The method ensures that each key has a corresponding value, by checking that there is a pair of elements (key, value)
     * in the `record` list.
     *
     * @return List<Map<String, String>> A list of maps, where each map contains key-value pairs for an individual hold case service.
     */
    public List<Map<String, String>> getHoldCaseServicesDisplayedOnMobile() {
        final List<Map<String, String>> mobSerDisplayed = new ArrayList<>();

        for (final WebElement ele : openedHold) {
            final List<WebElement> record = ele.findElements(By.xpath(".//button/div[contains(@class,'MuiBox-root css-men9xj')]/div/span"));

            final Map<String, String> servicesValues = new ConcurrentHashMap<>();
            for (int i = 0; i < record.size(); i = i + 2) {
                if (i + 1 < record.size()) {
                    final String key = record.get(i).getText();       // Get key text
                    final String value = record.get(i + 1).getText(); // Get value text
                    servicesValues.put(key, value);
                }
            }
            mobSerDisplayed.add(servicesValues);
        }
        return mobSerDisplayed;
    }

    /**
     * Retrieves the full details of an expanded hold case, including both the case details and associated service details,
     * as displayed on mobile.
     * This method combines the results from two separate data retrieval methods:
     * - `getHoldCaseDetailDisplayedOnMobile()` for fetching the hold case details.
     * - `getHoldCaseServicesDisplayedOnMobile()` for fetching the associated service details.
     * The two lists of hold case details and service details are merged into one list of maps, where each map contains
     * key-value pairs representing different aspects of the hold case and services.
     *
     * @return List<Map<String, String>> A merged list of maps containing both hold case details and service details.
     */
    public List<Map<String, String>> getActualHoldCaseDisplayedOnMobile() {
        final List<Map<String, String>> actualCaseDetail = getHoldCaseDetailDisplayedOnMobile();
        final List<Map<String, String>> actualHoldService = getHoldCaseServicesDisplayedOnMobile();

        final CommonUtilities commonUtilities = new CommonUtilities(driver,configProvider);
        return commonUtilities.mergeListOfMaps(actualCaseDetail, actualHoldService);
    }

    public void validateHoldCasePage(final String pageName,final String clickedCaseID) {
        try {
            final ApplicationPage applicationPage = PageFactory.createPage(driver, configProvider, pageName.trim());

            final String curURL = applicationPage.getCurrentUrl();
            final String[] partsURL = curURL.split("/");
            final String actualcaseID = partsURL[partsURL.length - 1];
            log.info("Expected CaseID : '{}' & ActualCaseIDINURL : '{}'", clickedCaseID, actualcaseID);
            if (actualcaseID.equals(clickedCaseID)) {
                waitToLoad();
            } else {
                fail("Failed to open correct page, Page is opened with " + actualcaseID);
            }
        } catch (ClassNotFoundException | InvocationTargetException | NoSuchMethodException | InstantiationException |
                 IllegalAccessException e) {
            log.error(e.getMessage());
            fail("Failed to load the page " + pageName);
        }
    }

    public void addInValueField(final String columnName,final String valueList) {
        final GenericPage genericElement = new GenericPage(driver,configProvider);
        final String[] val = valueList.split(",");

        final WebElement element = genericElement.getElementForFilterValue();
        waitAndClick(element);

        final Actions actions = new Actions(driver);

        if ("Status".equals(columnName.trim()) || "Hold Type".equals(columnName.trim())) {
            waitAndClick(element);
            for (final String valueToAdd : val) {
                final String value = valueToAdd.trim();
                waitAndClick(genericElement.getElementForFilterValue());
                waitAndClick(genericElement.getElementForFilterValuesToSelect(value));
                log.info("'{}' selected from dropdown", value);
            }
        } else {
            for (final String valueToAdd : val) {
                final String value = valueToAdd.trim();
                waitAndSendKeys(element, value);
                actions.sendKeys(element, Keys.ENTER).perform();
                log.info("'{}' added in Value textbox", value);
            }
        }
    }

    public void closeAllExistingHolds() {
        final APIClientFactory apiClientFactory = new APIClientFactory();
        final APIClient apiClient = apiClientFactory.generateAPIClient(configProvider.getProperty(Constants.API_ENVIRONMENT));
        final TestdataProcessor tdp = new TestdataProcessor(driver, configProvider);

        while (getHoldsCount() > 0) {
            final List<Map<String, String>> actualRecords = getActualHoldCaseDisplayed();
            tdp.closeAllHolds(actualRecords, apiClient);
            reloadPage();
        }
    }

    public Map<String, String> getFieldValuesOnPage(final String pageName, final String newCaseNumber, final List<String> selectedFields) {
        final List<Map<String, String>> dataList = getActualHoldCaseDisplayed();

        Map<String, String> valuesDisplayed = new ConcurrentHashMap<>();
        if (pageName.contains("Dashboard")) {
            for (final String fieldName : selectedFields) {
                final String fieldNameOnDashboard = Constants.HOLDS_DASHBOARD_ELEMENT.get(fieldName);
                final String fieldValue = getFieldFromListOfMap(dataList, fieldNameOnDashboard);
                valuesDisplayed.put(fieldName, fieldValue);
            }
        } else if (pageName.contains("Salesforce")) {
            final SalesforceManager sfManager = new SalesforceManager(driver, configProvider);
            valuesDisplayed = sfManager.getSFValueForCase(newCaseNumber, selectedFields);
        }
        return valuesDisplayed;
    }

    public String getFieldFromListOfMap(final List<Map<String, String>> dataList, final String fieldName) {
        return dataList.stream()
                .filter(dataMap -> dataMap.containsKey(fieldName))
                .map(dataMap -> dataMap.get(fieldName))
                .findFirst()
                .orElse("Field not found");
    }

    public boolean isHoldCasesExist() {
        return getHoldsCount() > 0;
    }

    public boolean isHoldCasesMatched(List<Map<String, String>> expectedHoldCases, List<Map<String, String>> actualHoldCases) {
        // Get the HoldCase Records displayed on dashboard without 'Time on Hold'
        final List<Map<String, String>> expHoldsWOTime = removeTimeOnHold(expectedHoldCases);
        List<Map<String, String>> actualHoldsWOTime = removeTimeOnHold(actualHoldCases);

        if (!expHoldsWOTime.equals(actualHoldsWOTime)) {
            log.error("Holds displayed on dashboard is not matched with expected Holds, Trying to update the records.");
            processTestData(actualHoldsWOTime, expHoldsWOTime);
            actualHoldsWOTime = removeTimeOnHold(getActualHoldCaseDisplayed());
            log.info("After Processed, Now Holds displayed on dashboard is '{}'", actualHoldsWOTime);
        }
        log.info("Comparing the Actual Records '{}' with Expected Records '{}'", actualHoldsWOTime, expHoldsWOTime);
        return actualHoldsWOTime.equals(expHoldsWOTime);
    }

    public boolean isListMatched(String tableName, List<String> expectedList) {
        List<String> actualList = null;
        if (tableName.contains("table header")) {
            actualList = getGridHeaderList();
        } else if (tableName.contains("toolbar")) {
            actualList = getDashboardToolbarOptionList();
        } else if (tableName.contains("footer services")) {
            actualList = getFooterServicesList();
        }
        log.info("Comparing the '{}' list with expected List '{}'", actualList, expectedList);
        return (actualList != null) && actualList.equals(expectedList);
        }

    public boolean holdCasesAreDisplayedInOrder(String expectedHolds) {
        final ScrollUtilities scrollUtilities = new ScrollUtilities(driver,configProvider);
        final SortingManager sortingManager = new SortingManager(driver,configProvider);

        // Split the string by comma & Convert the array to a List
        final String[] caseArray = expectedHolds.isEmpty() ? new String[0] : expectedHolds.split(COMMA_SPACE_REGEX);
        final List<String> expHoldsList = new ArrayList<>(Arrays.asList(caseArray));

        scrollUtilities.moveHorizontalScrollbarToLeft(getHorizontalScrollbars());

        final List<String> actSortedHolds = sortingManager.getSortedValueFromRecord(getActualHoldCaseDisplayed(),"Case Number");
        log.info("Comparing the holds '{}' list with expected Holds '{}'", actSortedHolds, expHoldsList);
        return actSortedHolds.isEmpty() && expHoldsList.isEmpty() || expHoldsList.equals(actSortedHolds);
    }

    public void uncheckColumnList(String columnsToUnSel) {
        final String[] columns = columnsToUnSel.isEmpty() ? new String[0] : columnsToUnSel.split(COMMA_SPACE_REGEX);
        for (final String columnName : columns) {
            clickOnColumnCheckbox(columnName);
        }
        log.info("columns '{}' is unchecked now.", columnsToUnSel);
    }

    public boolean holdCasesDetailsDisplayOnMobile(List<Map<String, String>> expectedHoldCases) {
        final List<Map<String, String>> actualHoldCases = getActualHoldCaseDisplayedOnMobile();

        // Get the HoldCase Records displayed on dashboard without 'Time on Hold'
        final List<Map<String, String>> expHoldsWOTime = removeTimeOnHold(expectedHoldCases);
        final List<Map<String, String>> actualHoldsWOTime = removeTimeOnHold(actualHoldCases);

        log.info("hold case detail displayed on mobile dashboard. '{}'", actualHoldCases);
        log.info("expected hold case detail displayed on mobile dashboard. '{}'", expectedHoldCases);
        return actualHoldsWOTime.equals(expHoldsWOTime);
    }

    public boolean mobileHoldCasesAreDisplayedInOrder(String expSortedHolds) {
        final ScrollUtilities scrollUtilities = new ScrollUtilities(driver,configProvider);
        final SortingManager sortingManager = new SortingManager(driver,configProvider);

        // Split the string by comma & Convert the array to a List
        final String[] caseArray = expSortedHolds.isEmpty() ? new String[0] : expSortedHolds.split(COMMA_SPACE_REGEX);
        final List<String> expHoldsList = new ArrayList<>(Arrays.asList(caseArray));

        scrollUtilities.moveHorizontalScrollbarToLeft(getHorizontalScrollbars());
        final List<String> actSortedHolds = sortingManager.getSortedValueFromRecordInMobile(getActualHoldCaseDisplayedOnMobile(),"Case Number");
        log.info("mobile hold case are displayed as '{}'", actSortedHolds);
        log.info("mobile hold case are expected to be displayed as '{}'", expHoldsList);

        return actSortedHolds.isEmpty() && expHoldsList.isEmpty() || expHoldsList.equals(actSortedHolds);
    }

    public void clickOnAnyMobileStatusTab() {
        final CommonUtilities commonUtilities = new CommonUtilities(driver, configProvider);
        final List<WebElement> statusEle = getMobileStatusTiles();
        final WebElement element = statusEle.get(commonUtilities.getRandomInteger(statusEle.size()));
        waitAndClick(element);
        log.info("'{}' status tab get clicked.", element.getText());
    }

    public boolean isHeaderMatched(String headerList) {
        final CommonUtilities commonUtilities = new CommonUtilities(driver,configProvider);
        final List<String> expHeaders = commonUtilities.getListBySplitString(headerList, COMMA_SPACE_REGEX);
        final List<String> actHeaders = getGridHeaderList();

        log.info("comparing '{}' header wth expected header '{}'", actHeaders, expHeaders);
        return actHeaders.equals(expHeaders);
    }
}