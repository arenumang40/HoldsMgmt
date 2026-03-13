package com.dhl.ecommerce.holdssbe.stepdefinitions;

import com.dhl.ecommerce.holdssbe.ConfigProvider;
import com.dhl.ecommerce.holdssbe.Constants;
import com.dhl.ecommerce.holdssbe.GlobalObjects;
import com.dhl.ecommerce.holdssbe.SharedContext;
import com.dhl.ecommerce.holdssbe.model.APICredentials;
import com.dhl.ecommerce.holdssbe.pages.HoldsCreatePage;
import com.dhl.ecommerce.holdssbe.pages.HoldsDashboardPage;
import com.dhl.ecommerce.holdssbe.services.*;
import com.dhl.ecommerce.holdssbe.utilities.CommonUtilities;
import com.dhl.ecommerce.holdssbe.utilities.FileUtilities;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import java.time.Duration;
import java.util.*;

import static org.testng.Assert.assertTrue;
import static org.testng.Assert.assertEquals;

@Slf4j
public class HoldsCreateSteps {

    protected WebDriver driver;
    protected ConfigProvider configProvider;
    protected WebDriverWait wait;
    protected APICredentials apiCredentials;

    private static final String COMMA_WHITESPACE_DELIMITER = ",\\s*";
    private static final String MISMATCH_MSG = " but found: ";
    private static final String EXPECTED_MSG = " Expected: ";

    public HoldsCreateSteps(final GlobalObjects globalObj) {
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

    @And("No Hold Cases detail exist")
    public void noHoldCasesDetailExist() {
        final HoldsDashboardPage dashboardPage = new HoldsDashboardPage(driver, configProvider);
        dashboardPage.closeAllExistingHolds();
    }

    @When("click on Pickup Customer Dropdown")
    public void clickOnPickupCustomerDropdown() {
        final HoldsCreatePage holdsCreate = new HoldsCreatePage(driver,configProvider);
        holdsCreate.clickONCustomersDropdown();
    }

    @And("selects any Pickup Customer from List")
    public void selectsAnyPickupCustomerFromList() {
        final HoldsCreatePage holdsCreatePage = new HoldsCreatePage(driver,configProvider);
        holdsCreatePage.selectRandomPickUpFromList();
    }

    @Given("selects a Pickup by clicking the dropdown icon")
    public void selectsAPickupByClickingTheDropdownIcon() {
        clickOnPickupCustomerDropdown();
        selectsAnyPickupCustomerFromList();
    }

    @Then("selected Pickup is displayed on Screen")
    public void selectedPickupIsDisplayedOnScreen() {
        final HoldsCreatePage holdsCreate = new HoldsCreatePage(driver,configProvider);
        final String displayedPickup = holdsCreate.getPickupDisplayedOnScreen();
        final String selectedPickup = SharedContext.getInstance().getSelectedPickup();
        assertEquals(displayedPickup, selectedPickup,
                "Pickup displayed on screen (" + displayedPickup +
                        ") is not the same as the selected (" + selectedPickup + ")");
    }

    @And("Short List of {string} is displayed")
    @Then("Full List of {string} is displayed")
    public void fullListOfIsDisplayed(final String listType, final List<String> expectedList) {
        final HoldsCreatePage holdsCreate = new HoldsCreatePage(driver,configProvider);
        final boolean status = holdsCreate.validateListWithExpectedList(listType, expectedList);
        assertTrue(status,"The " + listType + " list displayed does not match the expected list");
    }

    @When("selects any Hold Type")
    public void selectsAnyHoldType() {
        final HoldsCreatePage holdsCreate = new HoldsCreatePage(driver,configProvider);
        holdsCreate.waitToLoad();
        holdsCreate.waitTillTimeOut(Constants.SELENIUM_LONG_WAIT);
        holdsCreate.selectRandomHoldType();
    }

    @Then("selected HoldType is displayed on Screen")
    public void selectedHoldTypeIsDisplayedOnScreen() {
        final HoldsCreatePage holdsCreate = new HoldsCreatePage(driver,configProvider);
        final String displayedHoldType = holdsCreate.getHoldTypeDisplayedOnScreen();
        final String selectedHoldType = SharedContext.getInstance().getSelectedHoldType();
        assertEquals(displayedHoldType, selectedHoldType,
                "Pickup displayed on screen (" + displayedHoldType +
                        ") is not the same as the selected (" + selectedHoldType + ")");
    }

    @And("CREATE HOLD button is displayed")
    public void createHOLDButtonIsDisplayed() {
        final HoldsCreatePage holdsCreate = new HoldsCreatePage(driver,configProvider);
        assertTrue(holdsCreate.isCreateHoldButtonDisplayed(),"CREATE HOLD button is not displayed as expected");
    }

    @Then("user is on {string} selection step")
    public void userIsOnSelectionStep(final String sectionName) throws InterruptedException {
        final HoldsCreatePage holdsCreate = new HoldsCreatePage(driver,configProvider);
        assertTrue(holdsCreate.validateTheSelectionStep(sectionName),"User is not on the expected '" + sectionName + "' selection step. ");
    }

    @And("{string} is already selected")
    public void isHoldTypeSelected(String arg0) {
        final HoldsCreatePage holdsCreate = new HoldsCreatePage(driver,configProvider);
        final int count = holdsCreate.getSelectedHoldTypeCount();
        assertEquals(count, 1, "Either no hold type selected or multiple hold types selected");
    }

    @When("enter values in different fields")
    public void enterValuesInDifferentFields(final DataTable dataTable) {
        final List<String> fieldNameList = dataTable.asList(String.class);
        final HoldsCreatePage holdsCreate = new HoldsCreatePage(driver,configProvider);
        holdsCreate.addRandomValuesInFieldsToCreateHoldCase(fieldNameList);
    }

    @Given("using the Tab key only add the details on the new case creation page")
    @Given("fills in the details on the new case creation page")
    public void addDetailsToGenerateNewCase() {
        final HoldsCreatePage holdsCreate = new HoldsCreatePage(driver,configProvider);
        selectsAPickupByClickingTheDropdownIcon();
        selectsAnyHoldType();
        holdsCreate.waitTillTimeOut(Constants.SELENIUM_LONG_WAIT);
        final List<String> fieldNameList = new ArrayList<>(Constants.HOLDS_ELEMENT.keySet());
        holdsCreate.addRandomValuesInFieldsToCreateHoldCase(fieldNameList);
    }

    @And("added values is displayed in all fields")
    public void addedValuesIsdDisplayedInAllFields() {
        final CreateHoldFieldManager holds = new CreateHoldFieldManager(driver, configProvider);
        final Map<String, String> selectedFieldsValues = SharedContext.getInstance().getSelectedFieldsValues();
        final boolean status = selectedFieldsValues.equals(holds.getValuesDisplayedAgainstAllFields());
        assertTrue(status, "Selected fields Values are : " + selectedFieldsValues + "Actual displayed :" + holds.getValuesDisplayedAgainstAllFields());
    }

    @And("click on CREATE HOLD button")
    public void clickOnCREATEHOLDButton() {
        final HoldsCreatePage holdsCreate = new HoldsCreatePage(driver,configProvider);
        holdsCreate.clickOnCreateHoldButton();
    }

    @And("newly generated case number is visible")
    public void newlyGeneratedCaseNumberIsVisible() {
        final HoldsCreatePage holdsCreate = new HoldsCreatePage(driver,configProvider);
        holdsCreate.waitToLoad();
        holdsCreate.waitTillTimeOut(Constants.SELENIUM_IMPLICIT_WAIT);
        final String newCaseNumber = holdsCreate.getGeneratedCaseNumber();
        log.info("newly generated case number is : " + newCaseNumber);
        Assert.assertNotNull(newCaseNumber,"Newly generated case number"+newCaseNumber+" is not visible.");
    }

    @Then("able to add {string} file\\(s)")
    @And("upload the {string} file\\(s)")
    public void uploadTheFileS(final String fileNames) {
        final CommonUtilities commonUtilities = new CommonUtilities(driver,configProvider);
        final List<String> fieldNameList = commonUtilities.getListBySplitString(fileNames, COMMA_WHITESPACE_DELIMITER);
        final FileUtilities fileUtilities = new FileUtilities(driver, configProvider);
        fileUtilities.uploadFiles(fieldNameList);
    }

    @And("upload attachments")
    public void uploadAttachments(final DataTable dataTable) {
        final List<String> fieldNameList = dataTable.asList(String.class);
        final FileUtilities fileUtilities = new FileUtilities(driver, configProvider);
        fileUtilities.uploadFiles(fieldNameList);
    }

    @And("waits until the file(s) are uploaded")
    public void waitUntilTheFilesAreUploaded() {
        final HoldsCreatePage holdsCreate = new HoldsCreatePage(driver,configProvider);
        holdsCreate.waitTillFileUploaded();
    }

    @And("thumbnails of {string} file\\(s) is displayed on Screen")
    public void thumbnailsOfFileSShouldDisplayOnScreen(final String fileNames) {
        final HoldsCreatePage holdsCreate = new HoldsCreatePage(driver,configProvider);
        final CommonUtilities commonUtilities = new CommonUtilities(driver,configProvider);
        final List<String> fileNameList = commonUtilities.getListBySplitString(fileNames, COMMA_WHITESPACE_DELIMITER);
        assertTrue(holdsCreate.isAllThumbnailsPresent(fileNameList),"Not all thumbnails for the files " + fileNames + " are displayed on screen.");
    }

    @And("thumbnails of files should display on Screen")
    public void thumbnailsOfFilesShouldDisplayOnScreen(final DataTable dataTable) {
        final HoldsCreatePage holdsCreate = new HoldsCreatePage(driver,configProvider);
        final List<String> fieldNameList = dataTable.asList(String.class);
        assertTrue(holdsCreate.isAllThumbnailsPresent(fieldNameList),"Not all thumbnails for the files " + fieldNameList + " are displayed on screen.");
    }

    @Then("validation message {string} should be displayed for uploaded {string} file\\(s)")
    public void validationMessageShouldBeDisplayedForUploadedFileS(final String msg, final String fileNames) {
        final HoldsCreatePage holdsCreate = new HoldsCreatePage(driver,configProvider);
        final CommonUtilities commonUtilities = new CommonUtilities(driver,configProvider);

        final List<String> fileNameList = commonUtilities.getListBySplitString(fileNames, COMMA_WHITESPACE_DELIMITER);
        assertTrue(holdsCreate.areValidationMessagesPresentForAll(fileNameList, msg), "Validation message '" + msg + "' is not displayed for all the uploaded files: " + fileNameList);
    }

    @Then("added values should display against respective fields")
    public void addedValuesShouldDisplayAgainstRespectiveFields(final DataTable dataTable) {
        final List<String> fieldNameList = dataTable.asList(String.class);
        final CreateHoldFieldManager holds = new CreateHoldFieldManager(driver, configProvider);
        final Map<String, String> displayedValues = holds.getFieldValuesDisplayed(fieldNameList);
        final Map<String, String> selectedFieldsValues = SharedContext.getInstance().getSelectedFieldsValues();
        assertEquals(SharedContext.getInstance().getSelectedFieldsValues(), displayedValues,"Displayed field values do not match the selected values."+EXPECTED_MSG + selectedFieldsValues + MISMATCH_MSG + displayedValues);
    }

    @Then("able to add attachments")
    public void ableToAddAttachments(final DataTable dataTable) {
        uploadAttachments(dataTable);
        thumbnailsOfFilesShouldDisplayOnScreen(dataTable);
    }

    @Then("added values should display on {string} page against the fields")
    public void addedValuesShouldDisplayOnPageAgainstTheField(final String pageName, final DataTable dataTable) {
        final List<String> fieldNameList = dataTable.asList(String.class);
        final HoldsDashboardPage dashboardPage = new HoldsDashboardPage(driver, configProvider);
        final Map<String, String> displayedValues = dashboardPage.getFieldValuesOnPage(pageName, SharedContext.getInstance().getNewCaseNumber(), fieldNameList);
        assertEquals(SharedContext.getInstance().getSelectedFieldsValues(), displayedValues,"The displayed values on the " + pageName + " page do not match the expected selected field values."+ EXPECTED_MSG + SharedContext.getInstance().getSelectedFieldsValues() + MISMATCH_MSG + displayedValues);
    }

    @And("enters the value in different {string}")
    public void entersTheValueInDifferent(final String fieldList) {
        final CommonUtilities commonUtilities = new CommonUtilities(driver,configProvider);
        final List<String> fieldNameList = commonUtilities.getListBySplitString(fieldList, COMMA_WHITESPACE_DELIMITER);
       final HoldsCreatePage holdsCreate = new HoldsCreatePage(driver,configProvider);
       holdsCreate.addRandomValuesInFieldsToCreateHoldCase(fieldNameList);
    }

    @Then("added value is displayed on {string} application against {string}")
    @Then("added value should display on {string} page against {string}")
    public void addedValuesShouldDisplayOnPageAgainst(final String pageName, final String fieldList) {
        final CommonUtilities commonUtilities = new CommonUtilities(driver,configProvider);
        final List<String> fieldNameList = commonUtilities.getListBySplitString(fieldList, COMMA_WHITESPACE_DELIMITER);
        final HoldsDashboardPage dashboardPage = new HoldsDashboardPage(driver, configProvider);
        final Map<String, String> displayedValuesOn = dashboardPage.getFieldValuesOnPage(pageName, SharedContext.getInstance().getNewCaseNumber(), fieldNameList);
        final Map<String, String> selectedFieldsValues = SharedContext.getInstance().getSelectedFieldsValues();
        assertEquals(selectedFieldsValues, displayedValuesOn,"The displayed values on the " + pageName + " application/page do not match the expected selected field values. " +
                EXPECTED_MSG + selectedFieldsValues + MISMATCH_MSG + displayedValuesOn);
    }

    @Then("added value is displayed on {string} page against the {string}")
    public void addedValuesShouldDisplayOnPageAgainstThe(final String pageName, final String fieldList) {
        final CommonUtilities commonUtilities = new CommonUtilities(driver,configProvider);
        final List<String> fieldNameList = commonUtilities.getListBySplitString(fieldList, COMMA_WHITESPACE_DELIMITER);
        final HoldsDashboardPage dashboardPage = new HoldsDashboardPage(driver, configProvider);
        final Map<String, String> displayedValues = dashboardPage.getFieldValuesOnPage(pageName, SharedContext.getInstance().getNewCaseNumber(), fieldNameList);
        final Map<String, String> selectedFieldsValues = SharedContext.getInstance().getSelectedFieldsValues();
        assertEquals(selectedFieldsValues, displayedValues,"The added values on the " + pageName + " page do not match the expected field values. " +
                EXPECTED_MSG + selectedFieldsValues + MISMATCH_MSG + displayedValues);
    }

    @When("added value is displayed against the respective {string}")
    public void addedValuesShouldDisplayAgainstTheRespective(final String fields) {
        final CommonUtilities commonUtilities = new CommonUtilities(driver,configProvider);
        final List<String> fieldNameList = commonUtilities.getListBySplitString(fields, COMMA_WHITESPACE_DELIMITER);
        final CreateHoldFieldManager holds = new CreateHoldFieldManager(driver, configProvider);
        final Map<String, String> displayedValues = holds.getFieldValuesDisplayed(fieldNameList);
        final Map<String, String> selectedFieldsValues = SharedContext.getInstance().getSelectedFieldsValues();
        assertEquals(selectedFieldsValues, displayedValues,"The added values do not match the expected field values. " +
                EXPECTED_MSG + selectedFieldsValues +MISMATCH_MSG+ displayedValues);
    }

    @Then("item count values for each service {string} level is stored separately in SF")
    public void itemCountValuesForEachServiceLevelAreStoredSeparatelyInSF(final String fieldList) {
        final CommonUtilities commonUtilities = new CommonUtilities(driver,configProvider);
        final List<String> fieldNameList = commonUtilities.getListBySplitString(fieldList, COMMA_WHITESPACE_DELIMITER);
        fieldNameList.add("Service Count");

        final SalesforceManager sfManager = new SalesforceManager(driver, configProvider);
        final Map<String, String> sfFieldsValue = sfManager.getSFValueForCase(SharedContext.getInstance().getNewCaseNumber(), fieldNameList);

        final double expectedSerCount = sfManager.calculateExpectedServiceCount(sfFieldsValue);
        final double actualSerCount = Double.parseDouble(sfFieldsValue.get("Service Count"));
        assertEquals(expectedSerCount, actualSerCount,"The expected service count does not match the actual service count. " +
                EXPECTED_MSG + expectedSerCount + MISMATCH_MSG+ actualSerCount);
    }

    @And("enter the maximum value in different {string}")
    public void enterTheMaximumValueInDifferent(final String fieldName) {
        final CommonUtilities commonUtilities = new CommonUtilities(driver,configProvider);
        final List<String> fieldList = commonUtilities.getListBySplitString(fieldName, COMMA_WHITESPACE_DELIMITER);
        final CreateHoldFieldManager fieldManager = new CreateHoldFieldManager(driver, configProvider);
        fieldManager.addMaxValuesToHoldFields(fieldList);
    }

    @And("enters {string} in Pickup Customer textbox")
    public void entersInPickupCustomerTextbox(final String text) {
        final HoldsCreatePage holdsCreate = new HoldsCreatePage(driver,configProvider);
        holdsCreate.enterTextInPickupCustomerTextbox(text);
    }

    @Then("all pickup details containing {string} is displayed")
    public void allPickupDetailsContainingIsDisplayed(final String text) {
        final HoldsCreatePage holdsCreate = new HoldsCreatePage(driver,configProvider);
        assertTrue(holdsCreate.matchDisplayedPickupWithText(text),"Expected pickup details containing text '" + text + "' to be displayed, but they were not.");
    }

    @And("{string} file\\(s) is displayed against the Case on Salesforce")
    public void fileSIsDisplayedAgainstTheCaseOnSalesforce(final String fileNames) {
        final CommonUtilities commonUtilities = new CommonUtilities(driver,configProvider);
        final int expectedCount = commonUtilities.getListBySplitString(fileNames, COMMA_WHITESPACE_DELIMITER).size();
        final HoldsCreatePage holdsCreate = new HoldsCreatePage(driver,configProvider);
        final int actualCount = Integer.parseInt(holdsCreate.getAttachmentCount(SharedContext.getInstance().getNewCaseNumber(),apiCredentials));
        assertEquals(expectedCount, actualCount,"SF Attachment count '"+actualCount+"' does not matched with expected count '"+expectedCount+"'.");
    }

}