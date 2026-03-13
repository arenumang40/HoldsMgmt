package com.dhl.ecommerce.holdssbe;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.ImmutableCapabilities;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import static org.testng.FileAssert.fail;

public final class WebDriverFactory {

    private static final String CHROME_BROWSER = "Chrome";

    // Private constructor to prevent instantiation
    private WebDriverFactory() {
        throw new UnsupportedOperationException("Utility class should not be instantiated");
    }

    public static WebDriver createWebDriver(final ConfigProvider configProvider) {
        final WebDriver driver;
        final String runFromDocker = configProvider.getProperty(Constants.RUN_DOCKER);
        final String browserName = configProvider.getProperty(Constants.BROWSER_NAME);
        final String seleniumUrl = configProvider.getProperty(Constants.SELENIUM_URL).trim();
        final String browserStackUrl = configProvider.getProperty(Constants.BROWSERSTACK_URL).trim();

        final ImmutableCapabilities capabilities = createCapabilities(browserName);

        if (isRunningInDocker(runFromDocker)) {
            validateURL(seleniumUrl);
            driver = createRemoteWebDriver(seleniumUrl, capabilities);
        } else {
            validateURL(browserStackUrl);
            driver = createBrowserStackDriver(browserStackUrl,capabilities);
        }

        configureDriver(driver, configProvider);
        return driver;
    }

    private static void validateURL(final String url) {
        if (url == null) {
            fail("URL related environment variable is required.");
        }
    }

    private static boolean isRunningInDocker(final String runFromDocker) {
        return "true".equalsIgnoreCase(runFromDocker);
    }

    private static ImmutableCapabilities createCapabilities(final String browserName) {
        final ImmutableCapabilities capabilities;
        if (CHROME_BROWSER.equalsIgnoreCase(browserName)) {
            capabilities = getChromeCapabilities();
        } else {
            capabilities = new ImmutableCapabilities("browserName", browserName);
        }
        return capabilities;
    }

    private static ImmutableCapabilities getChromeCapabilities() {
        final Map<String, Object> prefs = new HashMap<>();
//        prefs.put("printing.print_preview_sticky_settings", false);
//        prefs.put("safebrowsing.enabled", "false");
        prefs.put("printing.print_preview_sticky_settings.appState", "{\"recentDestinations\":[{\"id\":\"Save as PDF\",\"origin\":\"local\",\"account\":\"\"}],\"selectedDestinationId\":\"Save as PDF\",\"version\":2}");
        //prefs.put("download.default_directory", "/path/to/download/directory");
        prefs.put("download.prompt_for_download", false);
        prefs.put("printing.default_destination_selection_rules", "{\"kind\":\"local\",\"namePattern\":\"Save as PDF\"}"); // Always open PDF externally

        final ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("--ignore-certificate-errors");
        chromeOptions.addArguments("--kiosk-printing");
        chromeOptions.addArguments("--no-proxy-server"); // Disables the proxy server
        chromeOptions.setExperimentalOption("prefs", prefs);

        return new ImmutableCapabilities(chromeOptions);
    }


    private static WebDriver createRemoteWebDriver(final String url, final ImmutableCapabilities capabilities) {
        WebDriver driver = null;
        try {
            driver = new RemoteWebDriver(new URI(url).toURL(), capabilities);
        } catch (URISyntaxException | MalformedURLException e) {
            fail("Failed to initialize WebDriver.", e);
        }
        return driver;
    }

    private static void configureDriver(final WebDriver driver, final ConfigProvider configProvider) {
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(configProvider.getIntegerProperty(Constants.SELENIUM_IMPLICIT_WAIT)));
        driver.manage().window().maximize();
    }

    private static WebDriver createBrowserStackDriver(final String browserstackUrl,ImmutableCapabilities capabilities) {
        WebDriver driver = null;  // Declare driver variable
        try {
            final Map<String, String> bstackOptions = new HashMap<>();
            capabilities = (ImmutableCapabilities) capabilities.merge(new ImmutableCapabilities("browserName", CHROME_BROWSER));
            capabilities = (ImmutableCapabilities) capabilities.merge(new ImmutableCapabilities("bstack:options", bstackOptions));

            final ChromeOptions chromeOptions = new ChromeOptions();
            chromeOptions.addArguments("--ignore-certificate-errors");
            capabilities = (ImmutableCapabilities) capabilities.merge(new ImmutableCapabilities(ChromeOptions.CAPABILITY, chromeOptions));

            driver = new RemoteWebDriver(new URL(browserstackUrl), capabilities);  // Assign to driver
        } catch (IOException e) {
            fail("Failed to initialize WebDriver for BrowserStack.", e);
        }
        return driver;  // Return the driver at the end
    }
}