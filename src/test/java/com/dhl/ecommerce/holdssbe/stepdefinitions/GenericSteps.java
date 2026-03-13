package com.dhl.ecommerce.holdssbe.stepdefinitions;

import com.dhl.ecommerce.holdssbe.ConfigProvider;
import com.dhl.ecommerce.holdssbe.Constants;
import com.dhl.ecommerce.holdssbe.GlobalObjects;
import com.dhl.ecommerce.holdssbe.SharedContext;
import com.dhl.ecommerce.holdssbe.model.APICredentials;
import com.dhl.ecommerce.holdssbe.pages.*;

import com.dhl.ecommerce.holdssbe.utilities.CommonUtilities;
import com.dhl.ecommerce.holdssbe.utilities.DockerUtilities;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.extern.slf4j.Slf4j;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.lang.reflect.InvocationTargetException;
import java.time.Duration;
import java.util.List;

import static org.testng.Assert.assertTrue;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertEquals;
import static org.testng.FileAssert.fail;

@Slf4j
public class GenericSteps {
    protected WebDriver driver;
    protected ConfigProvider configProvider;
    protected WebDriverWait wait;

    APICredentials apiCredentials;

    public GenericSteps(final GlobalObjects globalObj) {
        this.driver = globalObj.getDriver();
        this.configProvider = globalObj.getConfiguration();
        this.wait = new WebDriverWait(driver,
                Duration.ofSeconds(configProvider.getIntegerProperty(Constants.SELENIUM_IMPLICIT_WAIT)));
        this.apiCredentials = new APICredentials(
                configProvider.getProperty(Constants.CLIENT_ID),
                configProvider.getProperty(Constants.CLIENT_SECRET),
                configProvider.getProperty(Constants.SALESFORCE_USERNAME),
                configProvider.getProperty(Constants.SALESFORCE_PASSWORD));
    }

    @Given("on {string} page")
    public void onPage(final String pageName) {
        try {
            PageFactory.createPage(driver, configProvider, pageName.trim()).open();
        } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | InstantiationException |
                 IllegalAccessException e) {
            log.error(e.getMessage());
            e.printStackTrace(System.err);
            fail("Failed creating page " + pageName);
        }
    }

    @Then("page {string} is loaded")
    public void pageIsLoaded(final String pageName) {
        try {
            PageFactory.createPage(driver, configProvider, pageName.trim()).isLoaded();
        } catch (ClassNotFoundException | InvocationTargetException | NoSuchMethodException | InstantiationException | IllegalAccessException e) {
            log.error(e.getMessage());
            fail("Failed to load page " + pageName);
        }
    }

    @Then("stays on or is redirected to {string} page")
    public void stayOrRedirectedToPage(final String pageName) {
        try {
            Thread.sleep(1000L * Integer.parseInt(configProvider.getProperty(Constants.SELENIUM_IMPLICIT_WAIT)));
            PageFactory.createPage(driver, configProvider, pageName.trim()).isLoaded();
        } catch (ClassNotFoundException | InvocationTargetException | NoSuchMethodException | InstantiationException |
                 IllegalAccessException | InterruptedException e) {
            log.error(e.getMessage());
            fail("Failed to redirected on page " + pageName);
        }
    }

    @And("page title is {string}")
    public void pageTitleIs(final String title) {
        final String actualTitle = driver.getTitle();
        assertEquals(title.trim(), actualTitle, "Page title does not match.");
    }

    @And("{string} header is visible on page")
    public void headerIsDisplayed(final String header) {
        final HoldsDashboardPage dashboardPage = new HoldsDashboardPage(driver, configProvider);
        final GenericPage genericElement = new GenericPage(driver, configProvider);
        dashboardPage.waitToLoad();
        dashboardPage.waitTillTimeOut(Constants.SELENIUM_IMPLICIT_WAIT);
        final WebElement element = genericElement.getElementByRegText(header);
        assertTrue(dashboardPage.isElementDisplay(element), "'" + header + "' header should be visible on the page, but it is not.");
    }

    @When("user logs out and logs in")
    public void userLogsOutAndLogsIn() {
        final LoginPage loginPage = new LoginPage(driver, configProvider);
        loginPage.logOut();
        loginPage.logIn();
    }

    @When("user logged out by click on MyAccount icon")
    public void userLoggedOutByClickOnMyAccountIcon() {
        final ApplicationPage applicationPage = new LogoutPage(driver, configProvider);
        applicationPage.logOut();
    }

    @Then("{string} button is visible")
    @And("{string} is visible")
    public void buttonIsVisible(final String btn) {
        final HoldsDashboardPage dashboardPage = new HoldsDashboardPage(driver, configProvider);
        final GenericPage genericElement = new GenericPage(driver, configProvider);
        log.info("checking for '{}' button visibility.", btn);
        assertTrue(dashboardPage.isElementDisplay(genericElement.getElementByRegText(btn)), "'" + btn + "' button should be visible on the page, but it is not.");
    }

    @Then("{string} textbox is visible")
    public void textboxIsVisible(final String txt) {
        final HoldsDashboardPage dashboardPage = new HoldsDashboardPage(driver, configProvider);
        final GenericPage genericElement = new GenericPage(driver, configProvider);
        log.info("checking for '{}' textbox visibility.", txt);
        assertTrue(dashboardPage.isElementDisplay(genericElement.getElementByPlaceholderText(txt)), "'textbox " + txt + "' should be visible on the page, but it is not.");
    }

    @When("text {string} is entered in {string} box")
    public void textIsEnteredInBox(final String searchText,final String attributeName) {
        final HoldsDashboardPage dashboardPage = new HoldsDashboardPage(driver, configProvider);
        final GenericPage genericElement = new GenericPage(driver, configProvider);

        final WebElement element = genericElement.getElementByPlaceholderText(attributeName);
        dashboardPage.waitAndSendKeys(element, searchText);
        log.info("text '{}' is entered in '{}' box.", searchText, attributeName);
    }

    @Then("message {string} is displayed")
    @Then("message {string} display")
    public void messageDisplay(final String msg) {
        final GenericPage genericElement = new GenericPage(driver, configProvider);
        final List<WebElement> element = genericElement.getElementsByRegText(msg);
        log.info("checking for message '{}' visibility.", msg);
        assertFalse(element.isEmpty(), "message '" + msg + "' should be displayed, but it is not.");
    }

    @When("click on {string} icon")
    public void clickOnIcon(final String iconName) {
        final HoldsDashboardPage dashboardPage = new HoldsDashboardPage(driver,configProvider);
        final GenericPage genericElement = new GenericPage(driver, configProvider);
        dashboardPage.waitToLoad();
        final WebElement element = genericElement.getElementByDataTestId(iconName);
        dashboardPage.waitAndClick(element);
        log.info("clicked on '{}' icon.", iconName);
    }
    @When("clicks on {string} button")
    @When("click on {string} button")
    public void clickOnButton(final String btn) {
        final HoldsDashboardPage dashboardPage = new HoldsDashboardPage(driver, configProvider);
        final DockerUtilities dockerUtilities = new DockerUtilities(driver, configProvider);

        dashboardPage.handleButtonActions(btn, dockerUtilities, dashboardPage);
        dashboardPage.performButtonClick(btn);
    }

    @And("Select {string} after click on {string} field")
    public void selectAfterClickOnField(final String valueList,final String fromField) {
        final ApplicationPage applicationPage = new HoldsDashboardPage(driver, configProvider);
        final GenericPage genericElement = new GenericPage(driver, configProvider);
        applicationPage.waitAndClick(genericElement.getElementForFilterField(fromField));
        applicationPage.waitAndClick(genericElement.getElementForFilterValuesToSelect(valueList));
        log.info("'{}' selected after clicked on '{}'.", valueList, fromField);
    }

    @And("{string} list {string} is entered in Value field")
    public void listIsEnteredInValueField(final String columnName,final String valueList) {
        final HoldsDashboardPage dashboardPage = new HoldsDashboardPage(driver, configProvider);
        dashboardPage.addInValueField(columnName, valueList);
    }

    @Given("switch to mobile device")
    @Given("on mobile device")
    public void onDevice() {
        final HoldsDashboardPage dashboardPage = new HoldsDashboardPage(driver,configProvider);
        dashboardPage.switchToDevice(configProvider.getProperty(Constants.MOBILE_RESOLUTION));
    }

    @And("{string} icon is visible")
    public void iconIsVisible(final String iconValue) {
        final HoldsDashboardPage dashboardPage = new HoldsDashboardPage(driver,configProvider);
        final GenericPage genericElement = new GenericPage(driver, configProvider);
        final WebElement element = genericElement.getElementByDataTestId(iconValue);
        assertTrue(dashboardPage.isElementDisplay(element), "'" + iconValue + "' icon should be visible, but it is not.");
    }

    @When("click on any holdCase display on {string}")
    public void clickOnAnyHoldCaseDisplayOn(final String dashboardType) {
        final HoldsDashboardPage dashboardPage = new HoldsDashboardPage(driver, configProvider);
        final CommonUtilities commonUtilities = new CommonUtilities(driver, configProvider);

        final List<WebElement> holdCasesEle = dashboardPage.holdsElementDisplayOnDashboard(dashboardType);
        final WebElement element = holdCasesEle.get(commonUtilities.getRandomInteger(holdCasesEle.size()));

        SharedContext.getInstance().setClickedCaseNumber(element.getText());

        final String caseNumber = SharedContext.getInstance().getClickedCaseNumber();
        SharedContext.getInstance().setClickedCaseID(commonUtilities.getSFCaseID(caseNumber, dashboardType));

        dashboardPage.waitAndClick(element);
        log.info("'{}' get clicked", caseNumber);
    }

    @Then("page {string} is loaded for clicked holdCase")
    public void pageIsLoadedForClickedHoldCase(final String pageName) {
        final HoldsDashboardPage dashboardPage = new HoldsDashboardPage(driver,configProvider);
        dashboardPage.validateHoldCasePage(pageName, SharedContext.getInstance().getClickedCaseID());
    }

    @When("click on holdCase {string} display on {string}")
    public void clickOnHoldCase(final String caseNumber, final String dashboardType) {
        final HoldsDetailPage page = new HoldsDetailPage (driver,configProvider);
        page.clickOnCase(caseNumber, dashboardType);
        log.info("'{}' should clicked", caseNumber);
    }




}