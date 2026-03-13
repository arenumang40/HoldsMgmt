package com.dhl.ecommerce.holdssbe.pages;

import com.dhl.ecommerce.holdssbe.ConfigProvider;
import com.dhl.ecommerce.holdssbe.Constants;
import com.dhl.ecommerce.holdssbe.utilities.DockerUtilities;
import com.dhl.ecommerce.holdssbe.utilities.ScrollUtilities;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.*;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;

import static org.testng.Assert.fail;

@Slf4j
public class BasePage {
    private static final String EXPORT_BUTTON = "Export";
    private static final String PLACARD_BUTTON = "Placard";

    @FindBy(xpath = "//button[@data-testid='ph-btn-openDialogSelectFacility']")
    WebElement facilitySelectorButton;

    @FindBy(xpath = "//button[starts-with(@data-testid, 'dsf-btn-selectFacility')]")
    List<WebElement> facilityButtons;

    WebDriver driver;
    ConfigProvider configProvider;
    WebDriverWait webDriverWait;

    protected BasePage(final WebDriver driver, final ConfigProvider configPro){
        this.driver = driver;
        this.configProvider = configPro;
        PageFactory.initElements(driver,this);
        this.webDriverWait = new WebDriverWait(driver,
                Duration.ofSeconds(configProvider.getIntegerProperty(Constants.SELENIUM_IMPLICIT_WAIT)));
    }

    public void waitAndSendKeys(final WebElement elem, final String text) {
        try {
            webDriverWait.until(ExpectedConditions.elementToBeClickable(elem));
            elem.sendKeys(text);
        } catch (StaleElementReferenceException e) {
            log.debug("StaleElementReferenceException encountered. Re-fetching the element and retrying...");
            final WebElement freshElem = webDriverWait.until(ExpectedConditions.elementToBeClickable(elem)); // Refetch
            webDriverWait.until(ExpectedConditions.visibilityOf(freshElem));
            freshElem.sendKeys(text);
        }catch (TimeoutException e){
            fail("Waiting time is over, but not able to find the element '{}" + elem + "'");
        }
    }

    public void waitAndClick(final WebElement elem) {
        webDriverWait.until(ExpectedConditions.elementToBeClickable(elem));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", elem);
        elem.click();
    }

    public void clickOn(final WebElement elem) {
        elem.click();
    }

    public void waitTillVisible(final WebElement elem) {
        try{
            webDriverWait.until(ExpectedConditions.visibilityOf(elem));
        }catch (TimeoutException e){
            fail("Waiting time is Over but Element not found: "+ elem);
        }
    }

    public void waitTillTimeOut(final String timeOut) {
        try {
            Thread.sleep(configProvider.getIntegerProperty(timeOut)*1000L);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }

    public void waitToLoad() {
        final ExpectedCondition<Boolean> expectation = new ExpectedCondition<>() {
            private final Lock lock = new ReentrantLock(); // Lock to manage synchronization

            @Override
            public Boolean apply(final WebDriver webDriver) {
                lock.lock();
                try {
                    final String readyState = (String) ((JavascriptExecutor) webDriver)
                            .executeScript("return document.readyState", new Object[0]);
                    final String readyChangeState = (String) ((JavascriptExecutor) webDriver)
                            .executeScript("return document.readystatechange", new Object[0]);
                    // Swap literals to avoid possible NPE
                    return "complete".equals(readyState) || "interactive".equals(readyChangeState);
                } finally {
                    lock.unlock(); // Ensure lock is released even if an exception occurs
                }
            }
        };
        try {
            webDriverWait.until((Function) expectation);
        } catch (TimeoutException e) {
            fail("Page took too long to load.", e);
        }
    }

    public boolean isElementClickable(final String elementText) {
        final String genElementXpath = "//*[contains(text(),'" + elementText + "')]";
        boolean isClickable = false;
        try {
            final WebElement clickableElement = webDriverWait.until(ExpectedConditions.elementToBeClickable(By.xpath(genElementXpath)));
            log.info("Is element '{}' clickable? :'{}'", elementText,clickableElement.isDisplayed());
            isClickable = clickableElement.isDisplayed();
        } catch (NoSuchElementException e) {
            log.error("Error while waiting for element '{}'", elementText, e);
        }
        return isClickable;
    }

    public boolean isElementDisplay(final WebElement element) {
        boolean isDisplayed;
        try {
            isDisplayed = element.isDisplayed();
        } catch (NoSuchElementException | NullPointerException e) {
            isDisplayed = false;
        }
        return isDisplayed;
    }

    public boolean isElementNotDisplay(final String btn) {
        final List<WebElement> elements = driver.findElements(By.xpath(String.format("//*[text()='%s']", btn.trim())));
        return elements.isEmpty();
    }

    public boolean isToasterDisplay(final String txt) {
       boolean isMsgDisplayed = false;
        try {
            final WebElement toaster = webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='Toastify']")));
            final WebElement toasterElement = toaster.findElement(By.xpath(String.format("//*[text()='%s']", txt.trim())));
            isMsgDisplayed = isElementDisplay(toasterElement);
        } catch (NoSuchElementException | TimeoutException e) {
            log.debug("Getting Exception :{}",e.getMessage());
        }
        return isMsgDisplayed;
    }

    public void clickFacilitySelector() {
        waitAndClick(facilitySelectorButton);
    }

    public List<String> getFacilityCodeList() {
        final List<String> facilityCodeList = new ArrayList<>();
        waitTillVisible(facilityButtons.get(0));
        for (final WebElement facilityButton : facilityButtons) {
            facilityCodeList.add(facilityButton.findElement(By.xpath("div/p[1]")).getText());
        }
        return facilityCodeList;
    }

    public WebElement getFacilitySelectorButton() {
        waitTillVisible(facilitySelectorButton);
        return facilitySelectorButton;
    }

    /**
     * Selects a facility by its code.
     * This method locates the "Select Facility" button using the provided `facilityCode` and clicks on it.
     * The button is identified using a dynamic XPath based on the facility code.
     *
     * @param facilityCode The unique code of the facility to be selected.
     */
    public void selectFacility(final String facilityCode) {
        final WebElement element = driver.findElement(By.xpath(String.format("//button[@data-testid='dsf-btn-selectFacility-%s']", facilityCode.trim())));
        waitAndClick(element);
        waitToLoad();
    }

    public void handleButtonActions(final String btn, final DockerUtilities dockerUtilities, final HoldsDashboardPage dashboardPage) {
        if (EXPORT_BUTTON.equals(btn)) {
            dockerUtilities.deleteAllFilesAtDocker();
            log.info("All files from docker get deleted.");

            final ScrollUtilities scrollUtilities = new ScrollUtilities(driver, configProvider);
            scrollUtilities.moveHorizontalScrollbarToLeft(dashboardPage.getHorizontalScrollbars());
        } else if (btn.contains(PLACARD_BUTTON)) {
            dockerUtilities.deleteAllFilesAtDocker();
            log.info("All files from docker get deleted before extracting PLACARDs.");
        }
    }

    public void performButtonClick(final String btn) {
        final GenericPage genPage = new GenericPage(driver,configProvider);
        int retryCount = 0;
        final int maxRetries = 3;

        while (retryCount < maxRetries) {
            try {
                final WebElement element = genPage.getElementByRegText(btn);
                waitAndClick(element);
                log.info("clicked on '{}' button.", btn);
                break;
            } catch (StaleElementReferenceException e) {
                retryCount++;
                if (retryCount == maxRetries) {
                    log.error(e.getMessage());
                }
            }
        }
    }

    public void switchToDevice(final String resolution){
        log.info("Mobile Resolutions : " + resolution);

        // Check User-Agent to determine if mobile
        final String userAgent = ((RemoteWebDriver) driver).getCapabilities().getBrowserName();
        final boolean isMobile = userAgent.toLowerCase(Locale.ROOT).contains("mobile");

        if (!isMobile) {
            // Change resolution to a common mobile size
            final int width = Integer.parseInt(resolution.split("x")[0].trim());  //480
            final int height = Integer.parseInt(resolution.split("x")[1].trim()); //850
            driver.manage().window().setSize(new Dimension(width, height));
            log.info("Browser will work as Mobile with Dimension '{}X{}'", width, height);
        }
    }


}