package com.dhl.ecommerce.holdssbe.pages;

import com.dhl.ecommerce.holdssbe.ConfigProvider;
import com.dhl.ecommerce.holdssbe.Constants;
import com.dhl.ecommerce.holdssbe.SharedContext;
import com.dhl.ecommerce.holdssbe.api.APIClient;
import com.dhl.ecommerce.holdssbe.api.APIClientFactory;
import com.dhl.ecommerce.holdssbe.model.APICredentials;
import com.dhl.ecommerce.holdssbe.services.CreateHoldFieldManager;
import com.dhl.ecommerce.holdssbe.utilities.CommonUtilities;
import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.support.ui.ExpectedConditions;
import lombok.extern.slf4j.Slf4j;

import static org.testng.Assert.fail;

@Slf4j
public class HoldsCreatePage extends ApplicationPage {
    @FindBy(xpath = "//*[@data-testid='ArrowDropDownIcon']")
    WebElement customerDropdown;

    @FindBy(xpath = "//*[contains(@data-testid,'virtuoso-item-list')]/div[1]")
    WebElement firstPickup;

    @FindBy(xpath = "//textarea[@placeholder='Search']")
    WebElement customerSearch;

    @FindBy(xpath = "//div[@class='MuiListItemText-root MuiListItemText-multiline selected-item css-cbzvw']")
    WebElement pickupOnScreen;

    @FindBy(xpath = "//span[@class='MuiTypography-root MuiTypography-body1 css-i6gauy']/ancestor::label")
    WebElement holdTypeOnScreen;

    @FindBy(xpath = "//*[contains(@data-testid,'customer')]")
    List<WebElement> customerList;

//    @FindBy(xpath = "//*[@name='holdType']")
    @FindBy(xpath = "//*[contains(@class,'PrivateSwitchBase-input')]")
    List<WebElement> holdTypes;

    @FindBy(xpath = "//*[@type='radio']")
    List<WebElement> holdTypesRadio;

    @FindBy(xpath = "//*[@data-testid='pnh-btn-showMore']")
    WebElement showHoldTypes;

    @FindBy(xpath = "//button[@type='submit']")
    WebElement createHoldButton;

    @FindBy(xpath = "//*[starts-with(@class, 'MuiFormLabel-root MuiFormLabel-colorPrimary')]")
    List<WebElement> createHoldElements;

    @FindBy(xpath = "//*[@class='MuiStepIcon-text css-1pyl8xt']")
    List<WebElement> unfilledSection;

    @FindBy(xpath = "//*[@data-testid='CheckCircleIcon']")
    List<WebElement> filledSection;

    @FindBy(xpath = "//input[@type='radio' and @checked]")
    List<WebElement> countSelectedHoldType;

    @FindBy(xpath = "//*[@data-testid='hsc-text-holdId']")
    WebElement newHoldCaseNumber;

    @FindBy(xpath = "//*[text()='Uploading your attachment']")
    WebElement fileUploadedText;

    @FindBy(xpath = "//*[@data-test-id='pnh-textarea-searchCustomer']")
    WebElement pickupSearch;

    public HoldsCreatePage(final WebDriver driver, final ConfigProvider configProvider) {
        super(driver, configProvider);
        this.url = "/holds/new";
        this.title = "HOLDS MANAGEMENT";
        this.name = "Holds Create";
    }

    public void clickONCustomersDropdown() {
        waitAndClick(customerDropdown);
    }

    public List<String> getPickupCustomerList() {
        final List<String> pickupsList = new ArrayList<>();
        for (final WebElement pickupCustomer : customerList) {
            pickupsList.add(pickupCustomer.getText());
        }
        return pickupsList;
    }

    public List<String> getHoldTypeListDisplayed() {
        final List<String> actualList = new ArrayList<>();
        waitTillVisible(holdTypes.get(0));
        for (final WebElement holdType : holdTypes) {
            actualList.add(holdType.getAttribute("value"));
        }
        return actualList;
    }

    public void selectRandomPickUpFromList() {
        final CommonUtilities commonUtilities = new CommonUtilities(driver, configProvider);
        final List<String> pickupList = getPickupCustomerList();
        waitTillVisible(firstPickup);
        final int randomNumber = commonUtilities.getRandomInteger(pickupList.size()) % 4;
        waitAndClick(customerList.get(randomNumber));
        SharedContext.getInstance().setSelectedPickup(pickupList.get(randomNumber));
    }

    public String getPickupDisplayedOnScreen() {
        return (pickupOnScreen.isDisplayed() && pickupOnScreen.isEnabled())
                ? pickupOnScreen.getText()
                : "Element is not interactable";
    }

    public boolean validateListWithExpectedList(final String listType, final List<String> expectedList) {
        final List<WebElement> actualListToDisplay;
        final List<String> actualValue = new ArrayList<>();
        waitToLoad();
        waitTillTimeOut(Constants.SELENIUM_LONG_WAIT);
        switch (listType) {
            case "Hold Type":
                //waitTillVisible(holdTypes.get(0));
                actualListToDisplay = holdTypes;
               for (final WebElement we : actualListToDisplay) {
                    final String val = we.getAttribute("value");
                    actualValue.add(val);
               }
               break;
            case "Enter Hold Details":
                waitTillVisible(createHoldElements.get(0));
                actualListToDisplay = createHoldElements;
                for (final WebElement we : actualListToDisplay) {
                    final String val = we.getText();
                    actualValue.add(val);
                }
                break;
            default:
                throw new IllegalArgumentException("Unexpected list type: " + listType);
        }
        System.out.println("Check 13");
        return actualValue.equals(expectedList);
    }

//    public void selectRandomHoldType() {
//        final CommonUtilities commonUtilities = new CommonUtilities(driver, configProvider);
//        WebElement element = null;
//        int attempts = 0;
//        final int maxAttempts = 3;
//
//        while (attempts < maxAttempts) {
//            waitToLoad();
//            waitTillTimeOut(Constants.SELENIUM_LONG_WAIT);
//            try {
//                int holdTypesCount = holdTypes.size();
//                if (holdTypesCount > 0) {
//                    int index = commonUtilities.getRandomInteger(holdTypesCount);
//                    element = holdTypes.get(index);
//
//                    clickOn(element);
//                    SharedContext.getInstance().setSelectedHoldType(element.getAttribute("value"));
//                    return; // If successful, exit the loop
//                }
//            } catch (StaleElementReferenceException e) {
//                attempts++;
//                log.warn("Attempt {} of {} failed due to StaleElementReferenceException. Retrying...", attempts, maxAttempts);
//
//                if (attempts >= maxAttempts) {
//                    log.error("Failed to select hold type after {} attempts.", maxAttempts);
//                    throw e; // Rethrow the exception if all attempts fail
//                }
//                waitTillTimeOut(Constants.SELENIUM_IMPLICIT_WAIT);
//            }
//        }
//
//        if (element == null) {
//            log.error("Failed to select a hold type after {} attempts.", maxAttempts);
//        }
//    }

    public void selectRandomHoldType() {
        final CommonUtilities commonUtilities = new CommonUtilities(driver, configProvider);
        WebElement element = null;
        int attempts = 0;
        final int maxAttempts = 3;

        waitToLoad();
        waitTillTimeOut(Constants.SELENIUM_LONG_WAIT);

        while (attempts < maxAttempts) {
            try {
                int holdTypesCount = holdTypes.size();
                if (holdTypesCount > 0) {
                    clickOn(holdTypes.get(commonUtilities.getRandomInteger(holdTypesCount)));

                    waitTillVisible(holdTypeOnScreen);
                    SharedContext.getInstance().setSelectedHoldType(holdTypeOnScreen.getText().trim());
//                    int index = commonUtilities.getRandomInteger(holdTypesCount);
//                    element = holdTypes.get(index);
//                    clickOn(element);
//                    SharedContext.getInstance().setSelectedHoldType(element.getAttribute("value"));
                    return;
                } else {
                    log.error("No available hold types found.");
                    break;
                }
            } catch (StaleElementReferenceException e) {
                attempts++;
                log.warn("Attempt {} of {} failed due to StaleElementReferenceException. Retrying...", attempts, maxAttempts);

                if (attempts >= maxAttempts) {
                    log.error("Failed to select hold type after {} attempts due to StaleElementReferenceException.", maxAttempts);
                    throw e; // Rethrow the exception to fail the scenario
                }

                // Wait before retrying to allow for the element to stabilize
                waitTillTimeOut(Constants.SELENIUM_IMPLICIT_WAIT);
            }
        }

        // If no element was selected after all retry attempts, log an error
        if (element == null) {
            log.error("Failed to select a hold type after {} attempts.", maxAttempts);
        }
    }

    public String getHoldTypeDisplayedOnScreen() {
        return (holdTypeOnScreen.isDisplayed() && holdTypeOnScreen.isEnabled())
                ? holdTypeOnScreen.getText()
                : "Element is not interactable";
    }

    public boolean isCreateHoldButtonDisplayed() {
        waitTillVisible(createHoldButton);
        return isElementDisplay(createHoldButton);
    }

    public void clickOnCreateHoldButton() {
        clickOn(createHoldButton);
    }

    public int getFilledSectionCount() {
        return filledSection.size();
    }

    public int getUnfilledSectionCount() {
        return unfilledSection.size();
    }

    public int getSelectedHoldTypeCount() {
        return countSelectedHoldType.size();
    }

    public String getGeneratedCaseNumber() {
        String generatedCaseNumber = null;
        try {
            waitTillVisible(newHoldCaseNumber);
            generatedCaseNumber = newHoldCaseNumber.getText();
            SharedContext.getInstance().setNewCaseNumber(generatedCaseNumber);
        } catch (NoSuchElementException e) {
            fail("Element not found for new hold case number");
        }
        return generatedCaseNumber;
    }

    public void addRandomValuesInFieldsToCreateHoldCase(final List<String> fieldNameList) {
        final CreateHoldFieldManager holds = new CreateHoldFieldManager(driver, configProvider);
        waitToLoad();
        waitTillTimeOut(Constants.SELENIUM_LONG_WAIT);
        SharedContext.getInstance().setSelectedFieldsValues(holds.addValuesToCreateHoldFields(fieldNameList));
    }

    public void waitTillFileUploaded() {
        boolean isInvisible = false;
        while(!isInvisible)
        {
            try {
                isInvisible = webDriverWait.until(ExpectedConditions.invisibilityOf(fileUploadedText));
            } catch (TimeoutException e) {
                log.debug("Timeout occurred while checking file uploaded status.");
            }
        }
        waitToLoad();
    }

    public boolean isAllThumbnailsPresent(final List<String> attachments) {
        final GenericPage genericElement = new GenericPage(driver, configProvider);
        boolean allThumbnailsPresent = true;
        for (final String fileName : attachments) {
            if (genericElement.getElementForAttachmentThumbnail(fileName).isEmpty()) {
                allThumbnailsPresent = false;
            }
        }
        return allThumbnailsPresent;
    }

    public boolean matchDisplayedPickupWithText(final String text) {
        final List<String> displayedPickups = getPickupCustomerList();
        return displayedPickups.stream().allMatch(pickup -> pickup.contains(text));
    }

    public void enterTextInPickupCustomerTextbox(final String text) {
        waitAndSendKeys(pickupSearch, text);
    }

    public boolean areValidationMessagesPresentForAll(final List<String> fileNameList,final String msg) {
        return fileNameList.stream()
                .allMatch(fileName -> isToasterDisplay(String.format(msg, fileName)));
    }

    public String getHoldsCreatedDate(final String caseNumber,final APICredentials apiCredentials){
        final APIClientFactory apiClientFactory = new APIClientFactory();
        final APIClient apiClient = apiClientFactory.generateAPIClient(configProvider.getProperty(Constants.API_ENVIRONMENT));
        //return apiClient.getValueFromHoldDetailsAPI(caseNumber,"CreatedDate",apiCredentials);
        return apiClient.getValueFromHoldDetailsAPI(caseNumber,Constants.HOLDS_API_FIELDS.get("Create Date"),apiCredentials);
    }

    public String getAttachmentCount(final String caseNumber,final APICredentials apiCredentials){
        final APIClientFactory apiClientFactory = new APIClientFactory();
        final APIClient apiClient = apiClientFactory.generateAPIClient(configProvider.getProperty(Constants.API_ENVIRONMENT));
        return apiClient.getSFAttachmentCount(caseNumber,apiCredentials);
    }

    public boolean validateTheSelectionStep(final String sectionName) throws InterruptedException {
        boolean status = false;
        Thread.sleep(Integer.parseInt(configProvider.getProperty(Constants.SELENIUM_LONG_WAIT)) * 1000L);
        switch (sectionName) {
            case "Select Pickup Customer":
                status = getFilledSectionCount() == 0 && getUnfilledSectionCount() == 3;
                break;
            case "Select Hold Type":
                status = getFilledSectionCount() == 1 && getUnfilledSectionCount() == 2;
                break;
            case "Enter Hold Details":
                status = getFilledSectionCount() == 2 && getUnfilledSectionCount() == 1;
                break;
            default:
                throw new IllegalArgumentException("Unexpected section : " + sectionName);
        }
        log.info("(Filled sections: "+ getFilledSectionCount() +
                ", Unfilled sections: " + getUnfilledSectionCount()+")");
        return status;
    }
}