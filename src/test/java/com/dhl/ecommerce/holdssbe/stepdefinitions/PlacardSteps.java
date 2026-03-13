package com.dhl.ecommerce.holdssbe.stepdefinitions;

import com.dhl.ecommerce.holdssbe.ConfigProvider;
import com.dhl.ecommerce.holdssbe.Constants;
import com.dhl.ecommerce.holdssbe.GlobalObjects;
import com.dhl.ecommerce.holdssbe.SharedContext;
import com.dhl.ecommerce.holdssbe.model.APICredentials;
import com.dhl.ecommerce.holdssbe.pages.HoldsCreatePage;
import com.dhl.ecommerce.holdssbe.services.PlacardManager;
import com.dhl.ecommerce.holdssbe.utilities.CommonUtilities;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import java.time.Duration;
import java.util.Map;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

@Slf4j
public class PlacardSteps {
    protected WebDriver driver;
    protected ConfigProvider configProvider;
    protected WebDriverWait wait;
    protected APICredentials apiCredentials;

    private static final String DOWNLOAD = "Download";
    private static final String DOWNLOAD_FILE_PDF = "pdf";

    public PlacardSteps(final GlobalObjects globalObj) {
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

    @Then("{string} button is not visible")
    public void buttonIsNotVisible(final String btn) {
        final HoldsCreatePage holdsPage = new HoldsCreatePage(driver,configProvider);
        assertTrue(holdsPage.isElementNotDisplay(btn),String.format("The '%s' button should not be visible", btn));
    }

    @Then("PDF file is generated for placard with holds detail")
    public void pdfFileIsGeneratedForPlacardWithHoldsDetail() {
        final String directoryPath = configProvider.getFileDirectory(DOWNLOAD);
        final String fileName = configProvider.getFileName(DOWNLOAD_FILE_PDF);

        final PlacardManager placardManager = new PlacardManager(driver, configProvider);
        Assert.assertNotNull(placardManager.getPlacardContent(directoryPath, fileName), "Placard content should not be null.");
    }

    @And("Placard details should match the details in the application")
    public void placardDetailsShouldMatchTheDetailsInTheApplication() {
        final CommonUtilities commonUtilities = new CommonUtilities(driver,configProvider);
        final PlacardManager placardManager = new PlacardManager(driver, configProvider);

        final String directoryPath = configProvider.getFileDirectory(DOWNLOAD);
        final String fileName = configProvider.getFileName(DOWNLOAD_FILE_PDF);
        final Map<String, String> placardActual = placardManager.getPlacardDetails(directoryPath, fileName);
        final Map<String, String> placardExpected = placardManager.getExpectedValueForPlacardFromHoldCreate();

        assertTrue(commonUtilities.compareKeyValuePairInMaps(placardExpected, placardActual),"Expected placard details do not match the actual details in the application.");
    }


    @And("Only first {int} characters of description should display on placard")
    public void onlyFirstCharactersOfDescriptionShouldDisplayOnPlacard(int maxChars) {
        final PlacardManager placardManager = new PlacardManager(driver, configProvider);
        final String directoryPath = configProvider.getFileDirectory(DOWNLOAD);
        final String fileName = configProvider.getFileName(DOWNLOAD_FILE_PDF);
        final String expectedDesc = placardManager.getExpectedValueForPlacardFromHoldCreate().get("Description");
        final String actualDesc = placardManager.getPlacardDetails(directoryPath, fileName).get("Description");
        assertEquals(actualDesc, expectedDesc,"The description displayed on the placard does not match the expected truncated value.");
    }

    @Then("{string} added during {string} should display on Placard")
    public void addedDuringShouldDisplayOnPlacard(final String fieldName,final String pageName) {
        final PlacardManager placardManager = new PlacardManager(driver, configProvider);
        final String directoryPath = configProvider.getFileDirectory(DOWNLOAD);
        final String fileName = configProvider.getFileName(DOWNLOAD_FILE_PDF);
        final String actualValue = placardManager.getPlacardDetails(directoryPath, fileName).get(fieldName);
        final String expectedValue = SharedContext.getInstance().getSelectedFieldsValues().get(fieldName);
        assertEquals(actualValue, expectedValue,"The value for '" + fieldName + "' added during '" + pageName + "' did not match the expected value on the placard.");
    }

    @Then("holds creation date display as {string} on placard")
    public void holdsCreationDateDisplayAsMMDDOnPlacard(String dateFormat) {
        final CommonUtilities commonUtilities = new CommonUtilities(driver, configProvider);
        final PlacardManager placardManager = new PlacardManager(driver, configProvider);
        final String directoryPath = configProvider.getFileDirectory(DOWNLOAD);
        final String fileName = configProvider.getFileName(DOWNLOAD_FILE_PDF);
        final String actualDateMMDD = placardManager.getPlacardDetails(directoryPath, fileName).get("Date MM/DD");

        final HoldsCreatePage holdsPage = new HoldsCreatePage(driver,configProvider);
        final String holdCreatedDate = holdsPage.getHoldsCreatedDate(SharedContext.getInstance().getNewCaseNumber(),apiCredentials);
        final String expectedDateMMDD = commonUtilities.dateFormatMMDD(holdCreatedDate);

        assertEquals(actualDateMMDD, expectedDateMMDD,"The creation date displayed on the placard does not match the expected format MM/DD.");
    }
}