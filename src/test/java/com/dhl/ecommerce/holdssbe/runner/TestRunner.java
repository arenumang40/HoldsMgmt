package com.dhl.ecommerce.holdssbe.runner;

import io.cucumber.testng.*;
import lombok.extern.slf4j.Slf4j;

@CucumberOptions(
        features = {
                "src/test/resources/featurefiles/holds_login.feature",
                "src/test/resources/featurefiles/holds_facility.feature",
                "src/test/resources/featurefiles/holds_dashboard.feature",
                "src/test/resources/featurefiles/holds_create.feature",
                "src/test/resources/featurefiles/holds_attachment.feature",
                "src/test/resources/featurefiles/holds_placard.feature",
                "src/test/resources/featurefiles/holds_details.feature"
              },
        glue = {"com.dhl.ecommerce.holdssbe.stepdefinitions", "com.dhl.ecommerce.holdssbe.runner", "com.dhl.ecommerce.holdssbe.api"},
        plugin = {
                "pretty",
                "html:target/cucumber-reports/holdsMgmt.html",
                "json:target/cucumber-reports/cucumber.json",
                "junit:target/cucumber-reports/cucumber.xml"
        },
        //tags = "@UAS",
        monochrome = true
)

@Slf4j
public class TestRunner extends AbstractTestNGCucumberTests {
}