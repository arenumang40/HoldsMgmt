package com.dhl.ecommerce.holdssbe.api;

import com.dhl.ecommerce.holdssbe.model.APICredentials;
import java.util.Map;

public interface APIClient {
    String getAccessToken(APICredentials credentials);
    void createHoldCase(APICredentials credentials);
    boolean updateHoldCase(String caseID, Map<String, String> expectedRecord, APICredentials credentials);
    boolean closeHoldCase(String sfCaseID, APICredentials credentials);
    String getValueFromHoldDetailsAPI(String caseNumber, String fieldName, APICredentials credentials);
    String getSFAttachmentCount(String caseNumber, APICredentials apiCredentials);
}
