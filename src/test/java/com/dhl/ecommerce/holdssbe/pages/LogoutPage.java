package com.dhl.ecommerce.holdssbe.pages;

import com.dhl.ecommerce.holdssbe.ConfigProvider;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;

@Slf4j
public class LogoutPage extends ApplicationPage {

    public LogoutPage(final WebDriver driver, final ConfigProvider configProvider) {
        super(driver, configProvider);
        this.url = "/logout";
        this.title =  "HOLDS MANAGEMENT";
        this.name = "Holds Logout";
    }

}