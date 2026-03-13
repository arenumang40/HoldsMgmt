package com.dhl.ecommerce.holdssbe.stepdefinitions;

import com.dhl.ecommerce.holdssbe.ConfigProvider;
import com.dhl.ecommerce.holdssbe.Constants;
import com.dhl.ecommerce.holdssbe.GlobalObjects;
import com.dhl.ecommerce.holdssbe.pages.ApplicationPage;
import com.dhl.ecommerce.holdssbe.pages.HoldsDashboardPage;

import com.dhl.ecommerce.holdssbe.services.BGColorManager;
import com.dhl.ecommerce.holdssbe.services.SortingManager;
import com.dhl.ecommerce.holdssbe.utilities.FileUtilities;
import com.dhl.ecommerce.holdssbe.utilities.ScrollUtilities;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import lombok.extern.slf4j.Slf4j;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.*;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

@Slf4j
public class DashboardSteps {

    private static final String MISMATCH_MSG = ", but got: ";

    WebDriver driver;
    ConfigProvider configProvider;
    WebDriverWait wait;

    public DashboardSteps(final GlobalObjects globalObj) {
        this.driver = globalObj.getDriver();
        this.configProvider = globalObj.getConfiguration();
        this.wait = new WebDriverWait(driver,
                Duration.ofSeconds(configProvider.getIntegerProperty(Constants.SELENIUM_IMPLICIT_WAIT)));
    }

    @And("Hold Cases detail exist")
    public void holdCasesDetailExist(final DataTable dataTable) {
        final HoldsDashboardPage dashboardPage = new HoldsDashboardPage(driver,configProvider);

        // Get the HoldCase Records displayed on dashboard with 'Time on Hold'
        final List<Map<String, String>> expectedHoldCases = dataTable.asMaps(String.class, String.class);
        final List<Map<String, String>> actualHoldCases = dashboardPage.getActualHoldCaseDisplayed();
        assertTrue(dashboardPage.isHoldCasesMatched(expectedHoldCases,actualHoldCases),"The hold case records displayed on the dashboard do not match the expected records, Expected : "+expectedHoldCases+MISMATCH_MSG+actualHoldCases);
    }

    @And("page contains {string} list")
    public void pageContains(final String tableName,final  DataTable dataTable) {
        final HoldsDashboardPage dashboardPage =new HoldsDashboardPage(driver,configProvider);
        final List<String> expectedList = dataTable.asList(String.class);
        assertTrue(dashboardPage.isListMatched(tableName,expectedList),"The actual list of " + tableName + " does not match the expected list. Expected : "+expectedList);
    }

    @When("sorting cases by {string} in {string}")
    public void sortTheHoldCasesByColumnInOrder(final String columnName,final String order) {
        final SortingManager sortingManager = new SortingManager(driver,configProvider);
        sortingManager.sortRecordsByColumn(columnName, order);
        log.info("Holds get sorted for '{}' in '{}' order.", columnName, order);
    }

    @Then("Hold Cases are displayed in {string} order")
    @And("Hold Cases displayed in this order as sorted by TimeOneHold")
    public void holdCasesAreDisplayedInOrder(final String expectedHolds) {
        final HoldsDashboardPage dashboardPage = new HoldsDashboardPage(driver,configProvider);
        assertTrue(dashboardPage.holdCasesAreDisplayedInOrder(expectedHolds),"The actual sorted hold cases do not match the expected sorted order");
    }

    @Then("Hold Cases are sorted by status in order")
    public void holdCasesAreSortedByStatusInOrder(final DataTable table) {
        final HoldsDashboardPage dashboardPage = new HoldsDashboardPage(driver,configProvider);

        final List<String> expStatusOrders = table.transpose().asList();
        final List<String> actStatusOrders = dashboardPage.getGroupedFieldValueOrder("Status");

        log.info("Comparing the '{}' list with expected Holds '{}'", actStatusOrders, expStatusOrders);

        assertEquals(actStatusOrders, expStatusOrders, "The actual status order list does not match the expected order: Expected " + expStatusOrders + MISMATCH_MSG + actStatusOrders);
    }

    @And("it is possible to scroll to see all Hold Cases")
    public void itIsPossibleToScrollToSeeAllHoldCases() {
        final ScrollUtilities scrollUtilities = new ScrollUtilities(driver,configProvider);
        final HoldsDashboardPage page = new HoldsDashboardPage(driver,configProvider);
        scrollUtilities.scrollToEnd(page.getVerticalScrollbar());
    }

    @Then("{string} displays {string} cases")
    public void displaysServiceSum(final String service,final String expectedSumStr) {
        final HoldsDashboardPage dashboardPage = new HoldsDashboardPage(driver,configProvider);

        final int actualServiceSum = dashboardPage.getServiceSum(service);
        log.info("'{}' count is displaying as '{}'", service, actualServiceSum);
        assertEquals(Integer.parseInt(expectedSumStr), actualServiceSum, "The service count for '" + service + "' does not match the expected sum. Expected: " + expectedSumStr + MISMATCH_MSG + actualServiceSum);
    }

    @Then("column should display with checkbox as")
    public void columnListShouldDisplayWithCheckbox(final DataTable dataTable) {
        final HoldsDashboardPage dashboardPage = new HoldsDashboardPage(driver,configProvider);
        final List<Map<String, String>> expColumns = dataTable.asMaps();
        final List<Map<String, String>> actColumns = dashboardPage.getActualListOfColumnsWithCheckBox();

        log.info("Columns displayed with checkbox as : '{}'", actColumns);
        assertEquals(actColumns, expColumns, "The actual column list with checkboxes does not match the expected column list. Expected: " + expColumns + MISMATCH_MSG + actColumns);
    }

    @And("uncheck the {string} from column list")
    public void uncheckTheFromColumnlist(final String columnsToUnSel) {
        final HoldsDashboardPage dashboardPage = new HoldsDashboardPage(driver,configProvider);
        dashboardPage.uncheckColumnList(columnsToUnSel);
    }

    @Then("only {string} headers display on Page")
    public void onlyHeadersDisplayOnPage(final String headerList) {
        final HoldsDashboardPage dashboardPage = new HoldsDashboardPage(driver,configProvider);
        assertTrue(dashboardPage.isHeaderMatched(headerList),"The headers displayed on the page do not match the expected headers.");
    }

    @Then("holds should display as {string}")
    public void holdsShouldDisplayAs(final String densityStyle) {
        final HoldsDashboardPage dashboardPage = new HoldsDashboardPage(driver,configProvider);

        final boolean isMatched = dashboardPage.isDensityStyleMatched(densityStyle);
        assertTrue(isMatched, "The holds records are not displayed in the expected density style: " + densityStyle);
    }

    @Then("CSV file is generated with content displayed on screen")
    public void csvFileIsGeneratedWithContentDisplayedOnScreen(){
        final HoldsDashboardPage dashboardPage = new HoldsDashboardPage(driver,configProvider);
        final FileUtilities fileUtilities = new FileUtilities(driver,configProvider);

        final String directoryPath = configProvider.getFileDirectory("Download");
        final String csvFileName = configProvider.getFileName("csv");

        final List<Map<String, String>> expectedRecords = dashboardPage.getActualHoldCaseDisplayed();
        final List<Map<String, String>> actualFileRecords = fileUtilities.getCSVFileContent(directoryPath, csvFileName);

        log.info("comparing the CSV file records '{}' with Holds displayed at Dashboard '{}.", actualFileRecords, expectedRecords);
        assertEquals(actualFileRecords, expectedRecords,"The records in the CSV file do not match the holds displayed at the dashboard. Expected: " + expectedRecords + MISMATCH_MSG + actualFileRecords);
    }

    @Then("PDF file is generated with holds displayed on screen")
    public void pdfFileIsGeneratedWithHoldsDisplayedOnScreen() {
        final HoldsDashboardPage dashboardPage = new HoldsDashboardPage(driver,configProvider);
        final FileUtilities fileUtilities = new FileUtilities(driver, configProvider);
        final SortingManager sortingManager = new SortingManager(driver,configProvider);

        final String directoryPath = configProvider.getFileDirectory("Download");
        final String pdfFileName = configProvider.getFileName("pdf");

        final Set<String> expectedHoldSet = new HashSet<>(sortingManager.getSortedValueFromRecord(dashboardPage.getActualHoldCaseDisplayed(),"Case Number"));
        final Set<String> actualHoldSet = new HashSet<>(fileUtilities.getCaseFromPDF(directoryPath, pdfFileName));

        log.info("comparing the Holds present in PDF '{}' with Holds displayed at Dashboard '{}.", actualHoldSet, expectedHoldSet);
        assertEquals(expectedHoldSet, actualHoldSet, "Holds displayed in PDF do not match those displayed at the dashboard.");
    }

    @Then("Holds Background color should be as per status")
    public void holdsBackgroundColorShouldBeAsPerStatus(final DataTable dataTable) {
        final HoldsDashboardPage dashboardPage = new HoldsDashboardPage(driver,configProvider);
        final BGColorManager bgManager = new BGColorManager(driver,configProvider);

        final List<Map<String, String>> expStatusColor = dataTable.asMaps();
        final List<Map<String, String>> actHoldStsColor = bgManager.getActualStatusBgColorList(dashboardPage.getHoldCaseRows());
        final boolean isBGMatchedSts = bgManager.isBackgroundColorMatchForEachRecord(expStatusColor, actHoldStsColor);
        assertTrue(isBGMatchedSts, "Background color for holds does not match expected status colors. Expected: " + expStatusColor + MISMATCH_MSG + actHoldStsColor);
    }

    @Then("Status should display with Case Count and Color")
    public void statusShouldDisplayWithCaseCountAndColor(final DataTable dataTable) {
        final HoldsDashboardPage dashboardPage = new HoldsDashboardPage(driver,configProvider);
        final BGColorManager bgManager = new BGColorManager(driver,configProvider);

        final List<Map<String, String>> expTilesColor = dataTable.asMaps();
        final List<Map<String, String>> actTilesColor = bgManager.getActualStatusMobileBgColorList(dashboardPage.getMobileStatusTiles());

        log.info("Status is displaying with Holds Count Number in specific order as '{}'", actTilesColor);
        assertEquals(actTilesColor, expTilesColor,"Status tiles with case count and color do not match expected values. Expected: " + expTilesColor + MISMATCH_MSG + actTilesColor);
    }

    @Then("{string} Tile should be clickable")
    public void tileShouldBeClickable(final String statusTileName) {
        log.info("checking for '{}' tile is clickable ?", statusTileName);

        final ApplicationPage page = new HoldsDashboardPage(driver,configProvider);
        assertTrue(page.isElementClickable(statusTileName),"'" + statusTileName + "' tile should be clickable, but it is not.");
    }

    @When("click on {string} status tab")
    public void clickOnStatusTab(final String statusTabName) {
        final HoldsDashboardPage dashboardPage = new HoldsDashboardPage(driver,configProvider);
        dashboardPage.clickedStatusTab(statusTabName);
        log.info("'{}' status tab get clicked", statusTabName);
    }

    @Then("{string} subdivision get open")
    public void subdivisionGetOpen(final String expectedHoldTile) {
        final HoldsDashboardPage dashboardPage= new HoldsDashboardPage(driver,configProvider);

        final int expTilesCount = Integer.parseInt(expectedHoldTile);
        final int actTilesCount = dashboardPage.getOpenedHoldTileCount();

        log.info("'{}' holds detail get expanded.", actTilesCount);
        assertEquals(actTilesCount, expTilesCount,"The number of open subdivisions (" + actTilesCount + ") does not match the expected value (" + expTilesCount + ")."
        );
    }

    @And("Hold Cases detail display on Mobile")
    public void holdCasesDetailsDisplayOnMobile(final DataTable dataTable) {
        final HoldsDashboardPage dashboardPage = new HoldsDashboardPage(driver,configProvider);
        final List<Map<String, String>> expectedHoldCases = dataTable.asMaps(String.class, String.class);
        assertTrue(dashboardPage.holdCasesDetailsDisplayOnMobile(expectedHoldCases),"The hold cases displayed on mobile do not match the expected cases");
    }

    @Then("mobile Hold Cases are displayed in {string} order")
    public void mobileHoldCasesAreDisplayedInOrder(final String expSortedHolds) {
        final HoldsDashboardPage dashboardPage = new HoldsDashboardPage(driver,configProvider);
        assertTrue(dashboardPage.mobileHoldCasesAreDisplayedInOrder(expSortedHolds),"mobile hold case not displayed in expected order.");
    }

    @When("click on any status tab display on mobile dashboard")
    public void clickOnAnyStatusTabDisplayOnMobileDashboard() {
        final HoldsDashboardPage dashboardPage = new HoldsDashboardPage(driver, configProvider);
        dashboardPage.clickOnAnyMobileStatusTab();
    }
}