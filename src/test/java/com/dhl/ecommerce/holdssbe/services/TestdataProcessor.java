package com.dhl.ecommerce.holdssbe.services;

import com.dhl.ecommerce.holdssbe.ConfigProvider;
import com.dhl.ecommerce.holdssbe.Constants;
import com.dhl.ecommerce.holdssbe.api.APIClient;
import com.dhl.ecommerce.holdssbe.model.APICredentials;
import com.dhl.ecommerce.holdssbe.utilities.CommonUtilities;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
public class TestdataProcessor {

    protected WebDriver driver;
    protected ConfigProvider configProvider;
    protected CommonUtilities commonUtilities;
    protected APICredentials credentials;

    private static final String CASE_NUMBER = "Case Number";

    public TestdataProcessor(final WebDriver driver, final ConfigProvider configPro) {
        this.driver = driver;
        this.configProvider = configPro;
        this.commonUtilities = new CommonUtilities(this.driver, configProvider);
        this.credentials = new APICredentials(
                configProvider.getProperty(Constants.CLIENT_ID),
                configProvider.getProperty(Constants.CLIENT_SECRET),
                configProvider.getProperty(Constants.SALESFORCE_USERNAME),
                configProvider.getProperty(Constants.SALESFORCE_PASSWORD));
    }

    public void processActualRecord(final Map<String, String> actualRecord, final List<Map<String, String>> expectedList, final APIClient apiClient) {
        final String caseNumber = actualRecord.get(CASE_NUMBER);

        final Optional<Map<String, String>> matchingExpRecord = findMatchingRecord(actualRecord, expectedList);

        if (matchingExpRecord.isPresent()) {
            processMatchedRecord(actualRecord, matchingExpRecord.get(), apiClient, caseNumber);
        } else {
            processUnmatchedRecord(apiClient, caseNumber);
        }
    }

    private Optional<Map<String, String>> findMatchingRecord(final Map<String, String> actualRecord, final List<Map<String, String>> expectedList) {
        return expectedList.stream()
                .filter(expectedRecord -> actualRecord.get(CASE_NUMBER).equals(expectedRecord.get(CASE_NUMBER)))
                .findFirst();
    }

    private void processMatchedRecord(final Map<String, String> actualRecord, final Map<String, String> expectedRecord, final APIClient apiClient, final String caseNumber) {
        if (!actualRecord.equals(expectedRecord)) {
            log.info("For case #'{}', record does not match with expected dashboard holds. Trying to update it.", caseNumber);
            updateCase(apiClient, expectedRecord, caseNumber);
        }
    }

    private void updateCase(final APIClient apiClient, final Map<String, String> expectedRecord, final String caseNumber) {
        final String sfCaseID = commonUtilities.getSFCaseID(caseNumber, "web");
        final boolean apiStatus = apiClient.updateHoldCase(sfCaseID, expectedRecord, credentials);

        if (apiStatus) {
            log.info("Record of case #'{}' should now match with expected dashboard holds", caseNumber);
        } else {
            log.error("Unable to update the holds for case #'{}'", caseNumber);
        }
    }

    private void processUnmatchedRecord(final APIClient apiClient, final String caseNumber) {
        log.info("Case #'{}' should not be at dashboard holds. Trying to close this case.", caseNumber);
        closeHoldCase(apiClient, caseNumber);
    }

    private void closeHoldCase(final APIClient apiClient, final String caseNumber) {
        final String sfCaseID = commonUtilities.getSFCaseID(caseNumber, "web");

        final boolean apiStatus = apiClient.closeHoldCase(sfCaseID,credentials);

        if (apiStatus) {
            log.info("Case #'{}' is closed and should no longer be displayed on the dashboard.", caseNumber);
        } else {
            log.error("Unable to close the hold for case #'{}'", caseNumber);
        }
    }

    public void closeAllHolds(final List<Map<String, String>> actualRecords,final APIClient apiClient) {
        actualRecords.stream()
                .filter(record -> record.containsKey("Case Number"))
                .forEach(record -> {
                    final String caseNumber = record.get("Case Number");
                    processUnmatchedRecord(apiClient, caseNumber);
                });
    }


}