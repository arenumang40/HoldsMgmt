package com.dhl.ecommerce.holdssbe;

import lombok.Getter;
import org.openqa.selenium.WebDriver;

@Getter
public class GlobalObjects {

    WebDriver driver;
    ConfigProvider configuration;

    public GlobalObjects() {
        this.configuration = new ConfigProvider();
        this.driver = WebDriverFactory.createWebDriver(configuration);
    }

}