package com.dhl.ecommerce.holdssbe.pages;

import com.dhl.ecommerce.holdssbe.ConfigProvider;
import org.openqa.selenium.WebDriver;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

public final class PageFactory {

    private static final Map<String, String> PAGE_CLASSES;
    static {
        PAGE_CLASSES = Map.of(
                "Holds Login", "com.dhl.ecommerce.holdssbe.pages.LoginPage",
                "Holds Logout", "com.dhl.ecommerce.holdssbe.pages.LogoutPage",
                "Holds Dashboard", "com.dhl.ecommerce.holdssbe.pages.HoldsDashboardPage",
                "Holds Detail", "com.dhl.ecommerce.holdssbe.pages.HoldsDetailPage",
                "Holds Create", "com.dhl.ecommerce.holdssbe.pages.HoldsCreatePage"
        );
    }

    // Private constructor to prevent instantiation
    private PageFactory() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated.");
    }

    public static ApplicationPage createPage(final WebDriver driver, final ConfigProvider configProvider, final String pageName) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        final Class<?> clazz = Class.forName(PAGE_CLASSES.get(pageName));
        final Constructor<?> constructor = clazz.getConstructor(WebDriver.class, ConfigProvider.class);
        return (ApplicationPage) constructor.newInstance(driver, configProvider);
    }

}
