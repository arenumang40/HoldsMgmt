package com.dhl.ecommerce.holdssbe.utilities;

import com.dhl.ecommerce.holdssbe.ConfigProvider;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

@Slf4j
public class ScrollUtilities {
    protected WebDriver driver;
    protected ConfigProvider configProvider;

    public ScrollUtilities(final WebDriver driver, final ConfigProvider configProvider) {
        this.driver = driver;
        this.configProvider =  configProvider;
    }

    public void scrollToEnd(final WebElement table) {
        final JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
        final long viewportHeight = (Long) jsExecutor.executeScript("return window.innerHeight");
        final long tableHeight = (Long) jsExecutor.executeScript("return arguments[0].scrollHeight", table);
        if (tableHeight > viewportHeight) {
            log.info("Table height exceeds viewport height, scrolling necessary");
            scrollToBottom(driver);
        } else {
            log.info("Table height fits within the viewport, no scrolling necessary");
        }
    }

    public void scrollToBottom(final WebDriver driver) {
        final JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
        final long initialScrollPost = (Long) jsExecutor.executeScript("return window.scrollY");
        jsExecutor.executeScript("window.scrollTo(0, document.body.scrollHeight)");

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            log.error(e.getMessage());
        }

        final long finalScrollPost = (Long) jsExecutor.executeScript("return window.scrollY");
        if (finalScrollPost > initialScrollPost) {
            log.info("Scroll to bottom successfully.");
        } else {
            log.info("Scroll to bottom failed or already at bottom");
        }
    }

    public void scrollToTop(final WebDriver driver) {
        log.info("Attempting to scroll to the top of the page");
        final JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
        long initialScrollPos = (Long) jsExecutor.executeScript("return window.pageYOffset");
        long currentScrollPos = initialScrollPos;

        while (currentScrollPos > 0) {
            jsExecutor.executeScript("window.scrollTo(0, 0);");
            try {
                Thread.sleep(100); // Short delay to allow scroll to take effect
            } catch (InterruptedException e) {
                log.error("Interrupted while scrolling: " + e.getMessage());
                Thread.currentThread().interrupt();
            }
            currentScrollPos = (Long) jsExecutor.executeScript("return window.pageYOffset");

            if (currentScrollPos == initialScrollPos) {
                // If position hasn't changed, we're likely at the top or can't scroll further
                break;
            }
            initialScrollPos = currentScrollPos;
        }

        if (currentScrollPos == 0) {
            log.info("Successfully scrolled to the top of the page.");
        } else {
            log.warn("Could not scroll completely to the top. Final position: " + currentScrollPos);
        }
    }

    /**
     * Moves the horizontal scrollbar to the leftmost position.
     * This method uses JavaScript execution to set the `scrollLeft` property of the horizontal
     * scrollbar to `0`, effectively positioning the scrollbar at the leftmost side. It includes
     * a 10-second delay before and after performing the scroll operation to ensure the action completes.
     *
     * @throws InterruptedException If the current thread is interrupted while sleeping.
     */
    public void moveHorizontalScrollbarToLeft(final List<WebElement> elements) {
        try {
            if (elements.isEmpty()) {
                log.debug("Horizontal scrollbar not found, skipping the scroll action.");
                return; // Skip if the element is not found
            }
            Thread.sleep(10_000);
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollLeft = 0;", elements.get(0));
            Thread.sleep(10_000);
        } catch (InterruptedException e) {
            log.error("Error while moving the horizontal scrollbar to the left: ", e);
        }
    }
}