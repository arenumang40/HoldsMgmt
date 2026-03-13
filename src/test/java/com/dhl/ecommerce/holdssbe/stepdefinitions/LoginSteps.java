package com.dhl.ecommerce.holdssbe.stepdefinitions;

import com.dhl.ecommerce.holdssbe.ConfigProvider;
import com.dhl.ecommerce.holdssbe.GlobalObjects;
import com.dhl.ecommerce.holdssbe.pages.LoginPage;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;

@Slf4j
public class LoginSteps {
    WebDriver driver;
    ConfigProvider configProvider;

    public LoginSteps(final GlobalObjects globalObj) {
        this.driver = globalObj.getDriver();
        this.configProvider = globalObj.getConfiguration();
    }

    @Given("logged in")
    @When("user logs in")
    public void userLogsIn() {
        new LoginPage(driver, configProvider).logIn();
    }
}