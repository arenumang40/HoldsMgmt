package com.dhl.ecommerce.holdssbe.services;

import com.dhl.ecommerce.holdssbe.ConfigProvider;
import com.dhl.ecommerce.holdssbe.Constants;
import com.dhl.ecommerce.holdssbe.pages.GenericPage;
import com.github.javafaker.Faker;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.testng.FileAssert.fail;

@Slf4j
public class CreateHoldFieldManager {
    private static final int DESCRIPTION_LIMIT = 1000;
    private static final int DIVISOR_BOL = 1000;
    private static final int DIVISOR_DEFAULT = 3;

    protected WebDriver driver;
    protected ConfigProvider configProvider;
    final Faker faker = new Faker();

    public CreateHoldFieldManager(final WebDriver driver, final ConfigProvider configPro) {
        this.driver = driver;
        this.configProvider = configPro;
    }

    public Map<String, String> addValuesToCreateHoldFields(final List<String> fieldNames) {
        final Map<String, String> fieldValues = new ConcurrentHashMap<>();
        fieldNames.forEach(fieldName -> fieldValues.put(fieldName, generateRandomValueForField(fieldName)));
        log.info("Generated field values: {}", fieldValues);
        fillFieldsWithValues(fieldValues);
        return fieldValues;
    }

    private void fillFieldsWithValues(final Map<String, String> fieldValues) {
        final GenericPage genericPage = new GenericPage(driver,configProvider);
        final Map<String, String> holdsElement = Constants.HOLDS_ELEMENT;
        fieldValues.forEach((key, value) -> {
            final String fieldXpath = holdsElement.get(key);
            if (fieldXpath == null) {
                fail("Field mapping not found for: " + key);
                return;  // Skip if fieldXpath is not found
            }
            genericPage.waitAndSendKeys(genericPage.getCreateHoldElement(fieldXpath), value);
        });
    }

    private String generateRandomValueForField(final String fieldName) {
        final int maxLimit = Constants.FIELD_MAX_LIMITS.getOrDefault(fieldName, 0);
        return generateRandomValue(fieldName, maxLimit);
    }

    private String generateRandomValue(final String fieldName, final int maxLimit) {
        String valueToAdd;

        if (isNumberTextField(fieldName)) {
            final String digits = faker.number().digits(getMaxLimitForField(fieldName, maxLimit));
            valueToAdd = digits.startsWith("0") ? "1" + digits.substring(1) : digits;
        } else {
            final int minLength = "Description".equals(fieldName) ? 480 : 10;
            final int maxLength = getMaxLimitForField(fieldName,maxLimit);
            valueToAdd = faker.lorem().characters(minLength, maxLength).replace('f', 'b');
        }
        return valueToAdd;
    }

    private int getMaxLimitForField(final String fieldName,final int maxLimit) {
        final int limit;
        switch (fieldName) {
            case "Description":
                limit = DESCRIPTION_LIMIT;
                break;
            case "BOL/DSM Number":
                limit = maxLimit / DIVISOR_BOL;
                break;
            default:
                limit = maxLimit / DIVISOR_DEFAULT;
                break;
        }
        return limit;
    }

    private boolean isNumberTextField(final String fieldName) {
        return !("BOL/DSM Number".equals(fieldName) || "Description".equals(fieldName));
    }

    public void addMaxValuesToHoldFields(final List<String> fieldNames) {
        final GenericPage genericPage = new GenericPage(driver,configProvider);
        fieldNames.forEach(fieldName -> {
            final String fieldElement = Constants.HOLDS_ELEMENT.get(fieldName);
            final String randomValue = generateRandomValueWithMaxLimit(fieldName);
            genericPage.waitAndSendKeys(genericPage.getCreateHoldElement(fieldElement), randomValue);
        });
    }

    private String generateRandomValueWithMaxLimit(final String fieldName) {
        final int maxLimit = Constants.FIELD_MAX_LIMITS.getOrDefault(fieldName, 0);
        final String valueToAdd;

        if (isNumberTextField(fieldName)) {
            final String digits = faker.number().digits(maxLimit + 1);
            valueToAdd = digits.startsWith("0") ? "1" + digits.substring(1) : digits;
        } else {
            valueToAdd = faker.lorem().characters(0, maxLimit + 1);
        }
        return valueToAdd;
    }

    public Map<String, String> getValuesDisplayedAgainstAllFields() {
        final Map<String, String> valuesDisplayed = new ConcurrentHashMap<>();
        final GenericPage genericPage = new GenericPage(driver,configProvider);
        Constants.HOLDS_ELEMENT.forEach((key, value) -> {
            final String fieldValue = genericPage.getCreateHoldElement(value).getAttribute("value").trim();
            valuesDisplayed.put(key, fieldValue);
        });
        log.info("Values displayed: {}", valuesDisplayed);
        return valuesDisplayed;
    }

    public Map<String, String> getFieldValuesDisplayed(final List<String> selectedFields) {
        final Map<String, String> displayedValues = new ConcurrentHashMap<>();
        final GenericPage genericPage = new GenericPage(driver,configProvider);
        selectedFields.forEach(fieldName -> {
            final String fieldXpath = Constants.HOLDS_ELEMENT.get(fieldName);
            if (fieldXpath != null) {
                final String fieldValue = genericPage.getCreateHoldElement(fieldXpath).getAttribute("value").trim();
                displayedValues.put(fieldName, fieldValue);
            } else {
                log.warn("Warning: No XPath found for field: {}", fieldName);
            }
        });
        return displayedValues;
    }
}