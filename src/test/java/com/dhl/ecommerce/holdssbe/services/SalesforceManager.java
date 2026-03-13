package com.dhl.ecommerce.holdssbe.services;

import com.dhl.ecommerce.holdssbe.ConfigProvider;
import com.dhl.ecommerce.holdssbe.Constants;
import com.dhl.ecommerce.holdssbe.api.SalesforceAPIClient;
import com.dhl.ecommerce.holdssbe.model.APICredentials;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;

import java.util.concurrent.ConcurrentHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class SalesforceManager {

    protected WebDriver driver;
    protected ConfigProvider configProvider;
    protected SalesforceAPIClient apiClient;
    protected APICredentials apiCredentials;

    public SalesforceManager(final WebDriver driver, final ConfigProvider configPro) {
        this.driver = driver;
        this.configProvider = configPro;
        this.apiClient = new SalesforceAPIClient();
        this.apiCredentials = new APICredentials(
                configProvider.getProperty(Constants.CLIENT_ID),
                configProvider.getProperty(Constants.CLIENT_SECRET),
                configProvider.getProperty(Constants.SALESFORCE_USERNAME),
                configProvider.getProperty(Constants.SALESFORCE_PASSWORD)
        );
    }

    public Map<String,String> getSFValueForCase(final String caseNumber,final List<String> fieldList) {
         final Map<String, String> valuesDisplayed = new ConcurrentHashMap<>();
        for (final String fieldName : fieldList) {
            final String fieldNameInSF = Constants.HOLDS_API_FIELDS.get(fieldName);
            String fieldValue = apiClient.getValueFromHoldDetailsAPI(caseNumber, fieldNameInSF, apiCredentials);
            if ((fieldNameInSF.contains("Qty") || fieldNameInSF.contains("Total")) && fieldValue.contains(".0")){
                fieldValue = fieldValue.substring(0, fieldValue.indexOf('.'));
            }
            valuesDisplayed.put(fieldName, fieldValue);
        }
        return valuesDisplayed;
    }


    public double calculateExpectedServiceCount(final Map<String, String> sfFieldsValue) {
        return parseCount(sfFieldsValue, "MAX PC CNT")
                + parseCount(sfFieldsValue, "EXP PC CNT")
                + parseCount(sfFieldsValue, "GND PC CNT");
    }

    private double parseCount(final Map<String, String> sfFieldsValue, final String fieldName) {
        return Double.parseDouble(sfFieldsValue.getOrDefault(fieldName, "0"));
    }


}
