package com.dhl.ecommerce.holdssbe.pages;

import com.dhl.ecommerce.holdssbe.ConfigProvider;
import com.dhl.ecommerce.holdssbe.Constants;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

@Slf4j
public class LoginPage extends ApplicationPage {
    @FindBy(xpath = "//button[text()='Login with Azure AD']")
    WebElement loginButton;

    @FindBy(xpath = "//input[@type='email']")
    WebElement usernameField;

    @FindBy(xpath = "//input[@type='password']")
    WebElement passwordField;

    @FindBy(xpath = "//*[@value='Next']")
    WebElement nextButton;

    @FindBy(xpath = "//*[@type='submit']")
    WebElement submitButton;

    @FindBy(xpath = "//*[(text()='No') or (@value='No')]")
    WebElement stayLoggedInNoOpt;

    @FindBy(xpath = "//*[(text()='Yes') or (@value='Yes')]")
    WebElement stayLoggedInYes;

    public LoginPage(final WebDriver driver, final ConfigProvider configProvider) {
        super(driver, configProvider);
        this.url = "/login";
        this.title =  "HOLDS MANAGEMENT";
        this.name = "Holds Login";
    }

    public void logIn() {
        logIn(
                configProvider.getProperty(Constants.APPLICATION_USERNAME),
                configProvider.getProperty(Constants.APPLICATION_PASSWORD));
    }

    public void logIn(final String username, final String password) {
        if (!isUserLoggedIn()) {
            open();
            waitAndClick(loginButton);
            /* If we are already logged in now then skip the rest because user
            /* logged in before and it is not necessary to enter credentials
             */
            if (isUserLoggedIn()) {
                return;
            }
            signInUsingLdap(username, password);
        }
    }

    public void signInUsingLdap(final String email, final String password) {
        log.info("Working with ENVIRONMENT :"+configProvider.getProperty(Constants.APPLICATION_ENVIRONMENT));
        waitAndSendKeys(usernameField, email);
        waitAndClick(nextButton);
        waitAndSendKeys(passwordField, password);
        waitAndClick(submitButton);
        waitTillVisible(stayLoggedInNoOpt);
        waitAndClick(stayLoggedInNoOpt);
//        waitTillVisible(stayLoggedInYes);
//        waitAndClick(stayLoggedInYes);
        waitToLoad();
        waitForUrlToChange("/holds");
    }

    private void waitForUrlToChange(final String expectedUrl) {
        final WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(configProvider.getIntegerProperty(Constants.SELENIUM_IMPLICIT_WAIT)));
        wait.until((driver) -> driver.getCurrentUrl().endsWith(expectedUrl));
    }
}