package com.dhl.ecommerce.holdssbe.stepdefinitions;

import com.dhl.ecommerce.holdssbe.ConfigProvider;
import com.dhl.ecommerce.holdssbe.Constants;
import com.dhl.ecommerce.holdssbe.GlobalObjects;
import com.dhl.ecommerce.holdssbe.pages.HoldsDashboardPage;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.datatable.DataTable;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

@Slf4j
public class FacilitySteps {
    WebDriver driver;
    ConfigProvider configProvider;
    WebDriverWait wait;

    public FacilitySteps(final GlobalObjects globalObj) {
        this.driver = globalObj.getDriver();
        this.configProvider = globalObj.getConfiguration();
        this.wait = new WebDriverWait(driver,
                Duration.ofSeconds(configProvider.getIntegerProperty(Constants.SELENIUM_IMPLICIT_WAIT)));
    }

    @Then("Facility button is displayed on the top right corner")
    public void facilityButtonIsDisplayed() {
        final HoldsDashboardPage dashboardPage = new HoldsDashboardPage(driver, configProvider);
        wait.until(ExpectedConditions.visibilityOf(dashboardPage.getFacilitySelectorButton()));
        assertTrue(dashboardPage.getFacilitySelectorButton().isDisplayed(), "Facility button should be displayed on the top right corner, but it is not.");
    }

    @Then("{string} facility is displayed")
    @Given("{string} is the current facility")
    public void currentFacilityIsDisplayed(final String facilityCode) {
        final HoldsDashboardPage dashboardPage = new HoldsDashboardPage(driver, configProvider);
        dashboardPage.waitTillVisible(dashboardPage.getFacilitySelectorButton());
        final WebElement facility = dashboardPage.getFacilitySelectorButton().findElement(By.xpath("div"));
        wait.until(ExpectedConditions.visibilityOf(facility));
        assertEquals(facility.getText(), facilityCode.trim(),"The displayed facility does not match the expected facility code.");
    }

    @When("unrolling facility list")
    public void unrollingFacility() {
        final HoldsDashboardPage dashboardPage = new HoldsDashboardPage(driver, configProvider);
        dashboardPage.clickFacilitySelector();
    }

    @Then("a dynamic list of facilities is shown from masterdata")
    public void listOfFacilitiesIsDisplayed(final DataTable facilities) {
        final HoldsDashboardPage dashboardPage = new HoldsDashboardPage(driver,configProvider);
        final List<String> facilityCodes = dashboardPage.getFacilityCodeList();
        final List<String> expectedFacList = facilities.transpose().asList(String.class);
        assertEquals(facilityCodes, expectedFacList, "The facility codes displayed do not match the expected list from master data.");
    }

    @When("selects {string} from facility modal")
    public void selectsFacilityFromModal(final String facilityName) {
        final HoldsDashboardPage dashboardPage = new HoldsDashboardPage(driver,configProvider);
        dashboardPage.clickFacilitySelector();
        dashboardPage.selectFacility(facilityName);
    }
}