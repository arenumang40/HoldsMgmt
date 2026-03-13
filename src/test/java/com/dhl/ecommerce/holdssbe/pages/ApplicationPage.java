package com.dhl.ecommerce.holdssbe.pages;

import com.dhl.ecommerce.holdssbe.ConfigProvider;
import com.dhl.ecommerce.holdssbe.Constants;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.*;

import java.net.*;
import static org.testng.Assert.*;

@Slf4j
public class ApplicationPage extends BasePage {
    /**
     * Name of the page used to refer to it in feature files.
     */
    @Getter
    protected String name;

    /**
     * Page title
     */
    @Getter
    protected String title;

    /**
     * Relative page URL (the portion after host:port)
     */
    @Getter
    protected String url;

    @FindBy(xpath = "//button[@data-testId='ph-btn-openDialogMyAccount']")
    WebElement myAccountButton;

    @FindBy(xpath = "//a[@href='/logout']")
    WebElement logOutButton;

    @FindBy(xpath = "//button[text()='Cancel']")
    WebElement cancelButton;

    protected ApplicationPage(final WebDriver driver, final ConfigProvider configPro) {
        super(driver, configPro);
        PageFactory.initElements(driver, this);
    }

    /**
     * Navigates to this page in current browser window.
     */
    public void open() {
        final String fUrl = configProvider.getProperty(Constants.APPLICATION_BASE_URL) + getUrl();
        /* TODO: Check if we are already on this page. Navigate only if not. */
        try {
            webDriverWait.until(ExpectedConditions.urlContains(getUrl()));
        } catch (TimeoutException e) {
            /* This is not an error. */
            log.debug("Page not loaded already. Attempting to navigate.");
        }
        driver.get(fUrl.replace("//", "/"));
        isLoaded();
    }

    public void reloadPage() {
        driver.navigate().refresh();
    }

    /**
     * Compares URL and fails if page's URL does not match.
     */
    public void isLoaded() {
        webDriverWait.until(ExpectedConditions.urlContains(getUrl()));
        try {
            final URL current = new URI(getCurrentUrl()).toURL();
            assertEquals(current.getPath(), getUrl(), "The current URL path does not match the expected URL path.");
        } catch (URISyntaxException | MalformedURLException e) {
            fail("Failed to compare page URL with expected.");
        }
    }

    public String getCurrentTitle() {
        return driver.getTitle();
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    public boolean isUserLoggedIn() {
        boolean isLoggedIn = false;
        try {
            webDriverWait.until(ExpectedConditions.visibilityOf(myAccountButton));
            isLoggedIn = true;
        } catch (TimeoutException e) {
            log.debug("Timeout occurred while checking user login status.");
        }
        return isLoggedIn;
    }

    public void logOut() {
        if (isUserLoggedIn()) {
            waitAndClick(myAccountButton);
            waitAndClick(logOutButton);
            log.info("user logged out.");
        }
    }

    public void clickCancel() {
        waitAndClick(cancelButton);
        log.info("'Cancel' button get clicked.");
    }
}