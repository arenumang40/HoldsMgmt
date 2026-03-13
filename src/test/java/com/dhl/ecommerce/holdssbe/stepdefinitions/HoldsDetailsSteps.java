package com.dhl.ecommerce.holdssbe.stepdefinitions;

import com.dhl.ecommerce.holdssbe.ConfigProvider;
import com.dhl.ecommerce.holdssbe.Constants;
import com.dhl.ecommerce.holdssbe.GlobalObjects;
import com.dhl.ecommerce.holdssbe.SharedContext;
import com.dhl.ecommerce.holdssbe.api.APIClient;
import com.dhl.ecommerce.holdssbe.api.APIClientFactory;
import com.dhl.ecommerce.holdssbe.model.APICredentials;
import com.dhl.ecommerce.holdssbe.pages.HoldsDashboardPage;
import com.dhl.ecommerce.holdssbe.pages.HoldsDetailPage;
import com.dhl.ecommerce.holdssbe.services.SalesforceManager;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.testng.Assert.*;

@Slf4j
public class HoldsDetailsSteps {

    protected WebDriver driver;
    protected ConfigProvider configProvider;
    protected WebDriverWait wait;
    protected APICredentials apiCredentials;

    public HoldsDetailsSteps(final GlobalObjects globalObj) {
        this.driver = globalObj.getDriver();
        this.configProvider = globalObj.getConfiguration();
        this.wait = new WebDriverWait(driver,
                Duration.ofSeconds(configProvider.getIntegerProperty(Constants.SELENIUM_IMPLICIT_WAIT)));
        this.apiCredentials = new APICredentials(
                configProvider.getProperty(Constants.CLIENT_ID),
                configProvider.getProperty(Constants.CLIENT_SECRET),
                configProvider.getProperty(Constants.SALESFORCE_USERNAME),
                configProvider.getProperty(Constants.SALESFORCE_PASSWORD)
        );
    }

    @And("camera icon is visible")
    public void cameraIconIsVisible() {
        final HoldsDetailPage detailPage = new HoldsDetailPage(driver,configProvider);
        assertTrue(detailPage.isCameraIconVisible(),"Camera icon should be visible");
    }

    @When("click on camera icon")
    public void clickOnCameraIcon() {
        final HoldsDetailPage detailPage = new HoldsDetailPage(driver,configProvider);
        detailPage.clickOnCameraIcon();
    }

    @And("{string} displayed on holds detail page")
    public void displayedOnHoldsDetailPage(final String listName, final DataTable dataTable) {
        final HoldsDetailPage detailPage = new HoldsDetailPage(driver, configProvider);
        final List<String> expectedList = dataTable.asList(String.class);
        List<String> actualList = null;

        // Determine actual list based on the list name
        switch (listName.toLowerCase()) {
            case "services":
                actualList = detailPage.getServicesList();
                break;
            case "fields":
                actualList = detailPage.getHoldFieldsList();
                break;
            default:
                fail("UMANG -> displayedOnHoldsDetailPage() Unknown list name: " + listName);
                break;
        }

        // Check if the lists are equal (order doesn't matter)
        assertTrue(actualList != null && actualList.size() == expectedList.size() && actualList.containsAll(expectedList),
                "The Actual list " + actualList + " does not match with the expected list " + expectedList);
    }

    @And("buttons are visible on holds detail page")
    public void buttonsAreVisibleOnHoldsDetailPage(final DataTable dataTable) {
        final HoldsDetailPage detailPage = new HoldsDetailPage(driver,configProvider);
        final List<String> expectedButtons = dataTable.asList(String.class);
        assertTrue(detailPage.isButtonsVisibleOnHoldsDetailPage(expectedButtons));
    }

    @And("status should display on detail page in respected color")
    public void statusShouldDisplayOnDetailPageInRespectedColor() {
        final HoldsDetailPage detailPage = new HoldsDetailPage(driver,configProvider);
        final APIClientFactory apiClientFactory = new APIClientFactory();
        final APIClient apiClient = apiClientFactory.generateAPIClient(configProvider.getProperty(Constants.API_ENVIRONMENT));

        final String actualStatus = detailPage.getStatus();
        final String expectedStatus = apiClient.getValueFromHoldDetailsAPI(SharedContext.getInstance().getClickedCaseNumber(),"Status",apiCredentials).toUpperCase(Locale.ROOT);

        assertEquals(actualStatus, expectedStatus,"The actual status '"+actualStatus+"' on the detail page does not match the expected status'"+expectedStatus+"' from the API");
        assertTrue(detailPage.isStatusColorAsExpected(actualStatus),"The status color on the detail page does not match the expected color for the status");
    }

    @And("services values displayed on detail page should matched with clicked holdCase")
    public void servicesValuesDisplayedOnDetailPageShouldMatchedWithClickedHoldCase() {
        final HoldsDetailPage detailPage = new HoldsDetailPage(driver,configProvider);
        final SalesforceManager sfManager = new SalesforceManager(driver,configProvider);

        final Map<String,String> actualServiceDetails = detailPage.getServiceDetails();
        final List<String> keys = new ArrayList<>(actualServiceDetails.keySet());
        final Map<String,String> expectedServiceDetails = sfManager.getSFValueForCase(SharedContext.getInstance().getClickedCaseNumber(),keys);

        assertEquals(actualServiceDetails,expectedServiceDetails,"The service details on the detail page'"+actualServiceDetails+"' do not match the expected details from Salesforce'"+expectedServiceDetails+"'");
    }

    @And("fields values displayed on detail page should matched with clicked holdCase")
    public void fieldsValuesDisplayedOnDetailPageShouldMatchedWithClickedHoldCase() {
        final HoldsDetailPage detailPage = new HoldsDetailPage(driver,configProvider);
        final SalesforceManager sfManager = new SalesforceManager(driver,configProvider);

        final Map<String,String> actualDetails = detailPage.getHoldCaseDetails();
        final List<String> keys = new ArrayList<>(actualDetails.keySet());
        final Map<String,String> expectedDetails = sfManager.getSFValueForCase(SharedContext.getInstance().getClickedCaseNumber(),keys);

        assertTrue(detailPage.isDetailsMatchedWithSF(actualDetails,expectedDetails),"The field details on the detail page'"+actualDetails+"' do not match the expected details from Salesforce'"+expectedDetails+"'");
        //assertEquals(actualDetails,expectedDetails,"The field details on the detail page'"+actualDetails+"' do not match the expected details from Salesforce'"+expectedDetails+"'");
    }

    @And("fields values displayed on mobile detail page should matched with clicked holdCase")
    public void fieldsValuesDisplayedOnMobileDetailPageShouldMatchedWithClickedHoldCase() {
        final HoldsDetailPage detailPage = new HoldsDetailPage(driver,configProvider);
        final SalesforceManager sfManager = new SalesforceManager(driver,configProvider);

        final Map<String,String> actualDetails = detailPage.getHoldCaseDetailsOnMobile();
        final List<String> keys = new ArrayList<>(actualDetails.keySet());
        final Map<String,String> expectedDetails = sfManager.getSFValueForCase(SharedContext.getInstance().getClickedCaseNumber(),keys);

        assertTrue(detailPage.isDetailsMatchedWithSF(actualDetails,expectedDetails),"The field details on the detail page'"+actualDetails+"' do not match the expected details from Salesforce'"+expectedDetails+"'");
    }

    @And("{string} header is visible with clicked holdCase detail on page")
    public void headerIsVisibleWithClickedHoldCaseDetailOnPage(final String headerTxt) {
        final HoldsDetailPage page = new HoldsDetailPage(driver,configProvider);
        final String header = headerTxt+SharedContext.getInstance().getClickedCaseNumber();
        final boolean status = page.isCaseHeaderVisible(header);
        assertTrue(status, "header '" + header + "' should be visible on the page, but it is not.");
    }

    @And("BACK button display")
    public void backButtonDisplay() {
        final HoldsDetailPage detailPage = new HoldsDetailPage(driver,configProvider);
        assertTrue(detailPage.isBackButtonDisplayed(), "Back button is not displayed on Holds Detail Page");
    }

    @When("click on BACK button")
    public void clickOnBACKButton() {
        final HoldsDetailPage detailPage = new HoldsDetailPage(driver,configProvider);
        detailPage.clickOnBackButton();
    }

    @And("Hold Cases exist")
    public void holdCasesExist() {
        final HoldsDashboardPage dashboardPage = new HoldsDashboardPage(driver,configProvider);
        assertTrue(dashboardPage.isHoldCasesExist());
    }

    @Then("hold events are displayed")
    public void holdEventsAreDisplayed() {
        final HoldsDetailPage detailPage = new HoldsDetailPage(driver,configProvider);
        assertTrue(detailPage.isHoldEventDisplayed());
    }

    @And("it is possible to scroll to see all events")
    public void itIsPossibleToScrollToSeeAllEvents() {
        final HoldsDetailPage detailPage = new HoldsDetailPage(driver,configProvider);
        detailPage.scrollToViewEvents();
    }

    @And("most recent events {string} displayed on the bottom")
    public void mostRecentEventsDisplayedOnTheBottom(final String events) {
        final HoldsDetailPage detailPage = new HoldsDetailPage(driver,configProvider);
        detailPage.waitToLoad();
        detailPage.waitTillTimeOut(Constants.SELENIUM_LONG_WAIT);
        assertTrue(detailPage.isEventDisplayed(events),"'"+events+"' did not matched.");
    }

}