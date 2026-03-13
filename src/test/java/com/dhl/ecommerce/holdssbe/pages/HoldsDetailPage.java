package com.dhl.ecommerce.holdssbe.pages;

import com.dhl.ecommerce.holdssbe.ConfigProvider;
import com.dhl.ecommerce.holdssbe.Constants;
import com.dhl.ecommerce.holdssbe.SharedContext;
import com.dhl.ecommerce.holdssbe.utilities.CommonUtilities;
import com.dhl.ecommerce.holdssbe.utilities.ScrollUtilities;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.testng.FileAssert.fail;

@Slf4j
public class HoldsDetailPage extends ApplicationPage {
    @FindBy(xpath = "//*[@data-testid='phd-btn-open-camera-dialog']")
    WebElement cameraIcon;

    @FindBy(xpath = "//span[text()='Status']/preceding-sibling::h5")
    WebElement status;

    @FindBy(xpath = "//span[text()='Status']/parent::div")
    WebElement statusBox;

    @FindBy(xpath = "//*[contains(@class,'css-10r0f1g')]/div")
    List<WebElement> services;

    @FindBy(xpath = "//*[contains(@class,'css-1b8pexa')]/p")
    List<WebElement> caseDetails;

    @FindBy(xpath = "//*[contains(@class,'css-1qtavq7')]")
    List<WebElement> mobileDetailFields;

    @FindBy(xpath = "//*[contains(@class,'css-1c45nq7')]")
    List<WebElement> mobileDetailFieldsValues;

    @FindBy(xpath = "//*[contains(@data-testid, 'Back')]")
    WebElement backButton;

    @FindBy(xpath = "//*[contains(@class,'css-gu4ezc')]")
    List<WebElement> holdEvents;

    @FindBy(xpath = "//div[@data-testid='virtuoso-scroller']")
    WebElement verticalScrollbar;

    @FindBy(xpath = "//*[text()='Case Created']")
    WebElement caseCreated;

    public HoldsDetailPage(final WebDriver driver, final ConfigProvider configProvider) {
        super(driver,configProvider);
        this.title = "HOLDS MANAGEMENT";
        this.name = "CASE #";
    }

    public boolean isCameraIconVisible() {
        return isElementDisplay(cameraIcon);
    }

    public void clickOnCameraIcon() {
        waitAndClick(cameraIcon);
    }

    public String getStatus(){
        waitToLoad();
        waitTillVisible(status);
        return status.getText();
    }
    public String getStatusFontColor(){
        waitTillVisible(status);
        return status.getCssValue("color");
    }

    public String getStatusBoxBgColor(){
        waitTillVisible(statusBox);
        return statusBox.getCssValue("background-color");
    }

    public Map<String,String> getServiceDetails(){
        final Map<String,String> serviceDetails = new ConcurrentHashMap<>();
        for (final WebElement service : services) {
            final String key = service.findElement(By.xpath(".//span[1]")).getText();
            final String value = service.findElement(By.xpath(".//span[2]")).getText();
            serviceDetails.put(key, value);
        }
        return serviceDetails;
    }

    public Map<String,String> getHoldCaseDetails(){
        final Map<String,String> holdCaseDetails = new ConcurrentHashMap<>();
        for (int i=0; i<caseDetails.size(); i+=2){
            final String key = caseDetails.get(i).getText();
            final String value = caseDetails.get(i + 1).getText();
            holdCaseDetails.put(key, value);
        }
        return holdCaseDetails;
    }

    public Map<String,String> getHoldCaseDetailsOnMobile(){
        final Map<String,String> holdCaseDetails = new ConcurrentHashMap<>();
        for (int i=0; i<mobileDetailFields.size(); i++){
            final String key = mobileDetailFields.get(i).getText();
            final String value = mobileDetailFieldsValues.get(i).getText();
            holdCaseDetails.put(key, value);
        }
        return holdCaseDetails;
    }

    public boolean isStatusColorAsExpected(final String status){
        final String expectedFontColor = Constants.HOLDS_DETAIL_STS_FONT_COLOR.get(status);
        final String actualFontColor = getStatusFontColor();

        final String expectedBGColor = Constants.HOLDS_DETAIL_STS_BG_COLOR.get(status);
        final String actualBGColor = getStatusBoxBgColor();

        final boolean isFontColorMatched = expectedFontColor.equals(actualFontColor);
        final boolean isBGColorMatched = expectedBGColor.equals(actualBGColor);
        log.info("Status '{}', Font color '{}' & BG Color '{}'", status, actualFontColor, actualBGColor);
        return isFontColorMatched && isBGColorMatched;
    }

    public boolean isButtonsVisibleOnHoldsDetailPage(final List<String> expectedButtons) {
        final GenericPage genericPage = new GenericPage(driver, configProvider);
        boolean isAllButtonsPresent = true;
        for (final String button : expectedButtons) {
            final boolean isButtonVisible = isElementDisplay(genericPage.getElementByRegText(button));
            log.info("Button '{}' is {}", button, isButtonVisible ? "visible" : "not visible");
            if (!isButtonVisible) {
                isAllButtonsPresent = false;
                break;
            }
        }
        return isAllButtonsPresent;
    }

    public List<String> getServicesList() {
        final Map<String,String> actualServiceDetails = getServiceDetails();
        return new ArrayList<>(actualServiceDetails.keySet());
    }

    public List<String> getHoldFieldsList() {
        final Map<String,String> actualHoldDetails = getHoldCaseDetails();
        return new ArrayList<>(actualHoldDetails.keySet());
    }

    public boolean isCaseHeaderVisible(final String header) {
        waitTillTimeOut(Constants.SELENIUM_LONG_WAIT);
        final WebElement element = new GenericPage(driver, configProvider).getElementByRegText(header);

        boolean visible = false;
        if (element == null) {
            fail("Not able to find Header "+header+ " element.");
        }else {
            visible = isElementDisplay(element);
            log.info("'{}' is {}", header, visible ? "visible" : "not visible");
        }
        return visible;
    }

    public boolean isDetailsMatchedWithSF(final Map<String, String> actualDetails,final Map<String, String> expectedDetails) {
        actualDetails.remove("Time On Hold");
        expectedDetails.remove("Time On Hold");
        return actualDetails.equals(expectedDetails);
    }

    public boolean isBackButtonDisplayed() {
        return isElementDisplay(backButton);
    }

    public void clickOnBackButton() {
        waitAndClick(backButton);
    }

    public boolean isHoldEventDisplayed() {
        return !holdEvents.isEmpty();
    }

    public void scrollToViewEvents() {
        final ScrollUtilities scrollUtilities = new ScrollUtilities(driver,configProvider);
        scrollUtilities.scrollToTop(driver);
        scrollUtilities.scrollToEnd(verticalScrollbar);
    }

    public void clickOnCase(final String caseNumber,final String dashboardType) {
        final CommonUtilities commonUtilities = new CommonUtilities(driver, configProvider);
        final GenericPage page = new GenericPage(driver,configProvider);
        final WebElement element = page.getElementByRegText(caseNumber);
        waitAndClick(element);

        SharedContext.getInstance().setClickedCaseNumber(caseNumber.trim());
        SharedContext.getInstance().setClickedCaseID(commonUtilities.getSFCaseID(caseNumber.trim(), dashboardType));
    }

    public boolean isEventDisplayed(final String eventName) {
        waitTillVisible(holdEvents.get(0));
        final String actualEvent = holdEvents.get(holdEvents.size()-1).getText().trim();
        log.info("Last Event : {}",actualEvent);
        return actualEvent.equals(eventName.trim());
    }

}