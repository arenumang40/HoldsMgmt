package com.dhl.ecommerce.holdssbe.runner;

import com.dhl.ecommerce.holdssbe.GlobalObjects;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;

@Slf4j
public class Hooks {
    GlobalObjects globalObjects;
    WebDriver driver;

    public Hooks(final GlobalObjects globalObj) {
        this.globalObjects = globalObj;
        this.driver = globalObj.getDriver();
    }

    @Before
    public void before(final Scenario scenario) {
        log.info("\033[0;1m" + "Running scenario" + "     :::::::::::::::::::::::     '{}'     :::::::::::::::::::::::     ]",scenario.getName() );
    }

    @After
    public void tearDown(final Scenario scenario) {
        log.debug("Tearing down...");
        if (scenario.isFailed()) {
            captureScreenshot(scenario);
        }
        driver.quit();
        log.debug("Browser closed.");
        log.debug("Tear down completed.");
    }

    public void captureScreenshot(final Scenario scenario) {
        final byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
        scenario.attach(screenshot, "image/png", "failed");
        final File screenshotFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        final String ssDirectory = "target/screenshots/";
        final File destinationFile = new File(ssDirectory + "failed_scenario_" + System.currentTimeMillis() + ".png");
        try {
            FileUtils.copyFile(screenshotFile, destinationFile);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        log.info("Screenshot saved at '{}'", destinationFile.getAbsolutePath());
    }

}