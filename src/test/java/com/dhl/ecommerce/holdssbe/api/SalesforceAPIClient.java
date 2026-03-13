package com.dhl.ecommerce.holdssbe.api;

import browserstack.shaded.jackson.databind.ObjectMapper;
import com.dhl.ecommerce.holdssbe.ConfigProvider;
import com.dhl.ecommerce.holdssbe.Constants;
import com.dhl.ecommerce.holdssbe.model.APICredentials;
import com.dhl.ecommerce.holdssbe.model.CaseClosedDTO;
import com.dhl.ecommerce.holdssbe.model.CaseCreateDTO;
import com.dhl.ecommerce.holdssbe.model.CaseUpdateDTO;
import com.github.javafaker.Faker;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static io.restassured.config.EncoderConfig.encoderConfig;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

@Slf4j
public class SalesforceAPIClient implements APIClient {
    private static final Map<String, String> END_POINT_PATH = new ConcurrentHashMap<>();

    private final ConfigProvider configProvider = new ConfigProvider();
    private RequestSpecification request = RestAssured.given();
    private final String baseUrl;
    private final String tokenUrl;
    private final String apiVersion;

    private static final Faker FAKE_DATA = new Faker(new Locale("en-US"));

    // Define constants for repeated API keywords
    private static final String CREATE_CASE = "create case";
    private static final String UPDATE_CASE = "update case";
    private static final String CLOSED_CASE = "closed case";
    private static final String HOLD_CASE_DETAIL = "Hold Case detail";
    private static final String HOLDS = "holds";
    private static final String TOKEN = "token";
    private static final int HTTP_STATUS_NO_CONTENT = 204;
    private static final int HTTP_STATUS_OK = 200;
    private static final String EMPTY_PLACEHOLDER = "—";

    static {
        // Initialize endpoint paths
        END_POINT_PATH.put(TOKEN, "/services/oauth2/token");
        END_POINT_PATH.put(CREATE_CASE, "/services/data/{apiVersion}/sobjects/Case/");
        END_POINT_PATH.put(HOLD_CASE_DETAIL, "/services/data/{apiVersion}/sobjects/Case/");
        END_POINT_PATH.put(HOLDS, "/services/data/v60.0/query/?");
    }

    public SalesforceAPIClient() {
        this.apiVersion = configProvider.getProperty(Constants.API_VERSION);
        this.baseUrl = configProvider.getProperty(Constants.APPLICATION_API_BASE_URL);
        this.tokenUrl = baseUrl + END_POINT_PATH.get(TOKEN);

        // Ensure that the apiVersion & baseURL not null
        assertNotNull("API version should not be null", apiVersion);
        assertNotNull("Base URL should not be null", baseUrl);
    }

    /**
     * Generates an access token using Salesforce credentials via a `POST` request to the token endpoint.
     *
     * @param credentials the credentials (client ID, client secret, username, and password) for authentication
     * @return the access token as a String for subsequent API requests
     * @throws IllegalArgumentException if any of the credentials are null
     */
    @Override
    public String getAccessToken(@NonNull final APICredentials credentials) {
        RequestSpecification request = RestAssured.given().config(RestAssured.config().encoderConfig(encoderConfig()
                        .appendDefaultContentCharsetToContentTypeIfUndefined(false)))
                .contentType("application/x-www-form-urlencoded")
                .formParam("grant_type", "password")
                .formParam("client_id", credentials.getClientId())
                .formParam("client_secret", credentials.getClientSecret())
                .formParam("username", credentials.getUserName())
                .formParam("password", credentials.getPassword()).log().all();

        Response apiResponse = request.post(tokenUrl);
        JsonPath jsonPath = new JsonPath(apiResponse.asString());
        String accessToken = jsonPath.getString("access_token");
        if (accessToken == null || accessToken.isEmpty()) {
            fail("Access token is null or empty. API response: " + apiResponse.asString());
        }
        return jsonPath.getString("access_token");
    }

    /**
     * Creates a new hold case using provided credentials.
     *
     * @param credentials the credentials for authentication
     */
    @Override
    public void createHoldCase(@NonNull final APICredentials credentials) {
        String token = getAccessToken(credentials);

        try {
            String jsonInputString = getJSONBody(CREATE_CASE);
            request = buildRequest(CREATE_CASE, jsonInputString, token, null);
            Response apiResponse = request.post();
            JsonPath jsonPath = new JsonPath(apiResponse.asString());
            log.info("'{}' caseID generated successfully.", jsonPath.getString("id"));
        } catch (Exception e) {
            fail("Exception occurred during case creation.");
        }
    }

    /**
     * Updates an existing hold case with the provided case ID and expected record values.
     *
     * @param caseID         the ID of the hold case to update
     * @param expectedRecord the updated record data
     * @param credentials    the credentials for authentication
     * @return true if the case was successfully updated, false otherwise
     */
    @Override
    public boolean updateHoldCase(final String caseID, final Map<String, String> expectedRecord, @NonNull final APICredentials credentials) {
        String token = getAccessToken(credentials);
        boolean updateStatus;
        String jsonInputString = expectedRecord != null ? getTestDataJSONBody(sanitizeUpdatedRecord(expectedRecord)) : getJSONBody(UPDATE_CASE);
        request = buildRequest(HOLD_CASE_DETAIL, jsonInputString, token, caseID);
        Response apiResponse = request.patch();
        if (apiResponse.getStatusCode() == HTTP_STATUS_NO_CONTENT) {
            updateStatus = true;
            log.info("hold case updated successfully");
        } else {
            updateStatus = false;
            fail("Not able to update the hold case");
            log.error("getting '{}' status.", apiResponse.getStatusLine());
        }
        return updateStatus;
    }

    /**
     * Closes an existing hold case identified by the given case ID.
     *
     * @param caseID      the ID of the hold case to be closed
     * @param credentials the credentials for authentication
     * @return true if the hold case was successfully closed, false otherwise
     */
    @Override
    public boolean closeHoldCase(final String caseID, @NonNull final APICredentials credentials) {
        String token = getAccessToken(credentials);
        boolean closedStatus;
        String jsonInputString = getJSONBody(CLOSED_CASE);

        request = buildRequest(HOLD_CASE_DETAIL, jsonInputString, token, caseID);
        Response apiResponse = request.patch();

        if (apiResponse.getStatusCode() == HTTP_STATUS_NO_CONTENT) {
            closedStatus = true;
            log.info("hold case closed successfully");
        } else {
            closedStatus = false;
            log.error("getting '{}' status.", apiResponse.getStatusLine());
            fail("not able to close the hold case");
        }
        return closedStatus;
    }

    /**
     * Retrieves pickup details for the specified Pickup Number from the API.
     *
     * @param pickupNumber the pickup number to query
     * @return the response containing the pickup details
     */
    private Response getPickUpDetails(final String pickupNumber) {
        String token = getAccessToken(new APICredentials(
                configProvider.getProperty(Constants.CLIENT_ID),
                configProvider.getProperty(Constants.CLIENT_SECRET),
                configProvider.getProperty(Constants.SALESFORCE_USERNAME),
                configProvider.getProperty(Constants.SALESFORCE_PASSWORD)));

        String command = String.format("SELECT fields(all) FROM PickupLocation__c WHERE PickupAccountNumber__c='%s'  limit 10", pickupNumber);
        return request.baseUri(baseUrl).basePath(END_POINT_PATH.get(HOLDS))
                .header("Authorization", "Bearer " + token)
                .param("q", command).log().all().get();
    }

    /**
     * Builds the JSON body for a given API type (e.g., create, update, close).
     *
     * @param apiType the type of API operation (create, update, closed)
     * @return the corresponding JSON body as a string
     */
    private String getJSONBody(final String apiType) {
        String jsonBody;
        switch (apiType) {
            case CREATE_CASE:
                jsonBody = getJsonDataFromObject(new CaseCreateDTO());
                break;
            case UPDATE_CASE:
                jsonBody = getJsonDataFromObject(getDefaultUpdatedCaseObject());
                break;
            case CLOSED_CASE:
                jsonBody = getJsonDataFromObject(new CaseClosedDTO());
                break;
            default:
                jsonBody = "{}";
                break;
        }
        return jsonBody;
    }

    /**
     * Constructs a JSON string from an object using Jackson's ObjectMapper.
     *
     * @param caseObj the object to be converted to a JSON string
     * @return the JSON string representation of the object
     */
    private String getJsonDataFromObject(final Object caseObj) {
        String jsonBody = "{}";
        try {
            jsonBody = new ObjectMapper().writeValueAsString(caseObj);
        } catch (RuntimeException e) {
            log.error("Error converting object to JSON", e);
        }
        return jsonBody;
    }

    /**
     * Extracts a specified field value from a JSON API response.
     *
     * @param field       the field to extract from the JSON response
     * @param apiResponse the API response containing the data
     * @return the extracted field value as a string
     * @throws AssertionError if no records are found or the status code is not OK
     */
    private String extractFromResponse(final String field, final Response apiResponse) {
        String fieldValue = null;
        if (apiResponse.getStatusCode() == HTTP_STATUS_OK) {
            String jsonResponse = apiResponse.asString();
            JsonPath jsonPath = new JsonPath(jsonResponse);
            if (jsonPath.getInt("totalSize") > 0) {
                fieldValue = jsonPath.getString("records[0]." + field);
            } else {
                fail("No records found in API Response.");
            }
        } else {
            fail("Getting unexpected status code: " + apiResponse.getStatusCode());
            log.error("Unexpected status code: {} - {}", apiResponse.getStatusCode(), apiResponse.getStatusLine());
        }
        return fieldValue;
    }

    /**
     * Builds the request specification for making an API request, including the appropriate endpoint, body, token and CaseId.
     *
     * @param endpointKey     the endpoint key (create case, update case, etc.)
     * @param jsonInputString the JSON input body to send in the request
     * @param token           the authorization token to include in the request header
     * @param caseID          the case ID (if any) to append to the endpoint path
     * @return the configured RequestSpecification for the API request
     */
    private RequestSpecification buildRequest(final String endpointKey, final String jsonInputString,final String token, final String caseID) {
        return RestAssured.given()
                .config(RestAssured.config().encoderConfig(encoderConfig()
                        .appendDefaultContentCharsetToContentTypeIfUndefined(false))).baseUri(baseUrl).basePath(END_POINT_PATH.get(endpointKey).replace("{apiVersion}", apiVersion) + (caseID != null ? caseID : ""))
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(jsonInputString).log().all();
    }

    /**
     * Sanitizes the provided map of expected record values by replacing any instances of "—" with null.
     * @param expectedRecord the map of expected record values
     * @return a sanitized map where "—" values are replaced with null
     */
    private Map<String, String> sanitizeUpdatedRecord(final Map<String, String> expectedRecord) {
        Map<String, String> updatedRecord = new HashMap<>();
        for (Map.Entry<String, String> entry : expectedRecord.entrySet()) {
            String value = entry.getValue();
            if (EMPTY_PLACEHOLDER.equals(value)) {
                updatedRecord.put(entry.getKey(), null);  // Set to null if value is "—"
            } else {
                updatedRecord.put(entry.getKey(), value);  // Otherwise, keep the original value
            }
        }
        return updatedRecord;
    }

    /**
     * Returns a default updated case object to be used for updating a case.
     * The returned object will contain randomly generated values for various fields.
     * @return a populated CaseUpdateAPIPage object with default values
     */
    private CaseUpdateDTO getDefaultUpdatedCaseObject() {
        CaseUpdateDTO caseObj = new CaseUpdateDTO();
        caseObj.setAccountId("001bh000006nN4rAAE");
        caseObj.setNA_Pickup_Location__c("a1Qbh0000006WzhEAE");
        caseObj.setNA_Mail_Terminal__c("DGM Phoenix - PHX");
        caseObj.setStatus(Constants.HOLD_STATUS.get(getRandomInteger(Constants.HOLD_STATUS.size() - 1) + 1));
        caseObj.setNA_Hold_Type__c(Constants.HOLD_TYPE.get(getRandomInteger(Constants.HOLD_TYPE.size() - 1) + 1));
        caseObj.setNA_BOL_Number__c(String.valueOf(FAKE_DATA.number().numberBetween(0, 99)));
        caseObj.setNA_Total_Pallet_Count_On_Placard__c(String.valueOf(FAKE_DATA.number().numberBetween(0, 99)));
        caseObj.setQty_Of_Expedited_MAX_items__c(String.valueOf(FAKE_DATA.number().numberBetween(0, 99)));
        caseObj.setQty_Of_Expedited_items__c(String.valueOf(FAKE_DATA.number().numberBetween(0, 99)));
        caseObj.setQty_Of_Ground_items__c(String.valueOf(FAKE_DATA.number().numberBetween(0, 99)));
        caseObj.setNA_Qty_Of_International_Items__c(String.valueOf(FAKE_DATA.number().numberBetween(0, 99)));
        return caseObj;
    }

    /**
     * Prepares the test data for updating a case based on the provided case number and expected record values.
     * @param expectedRecord a map of expected record values to be included in the update
     * @return the JSON body for the case update request
     */
    private String getTestDataJSONBody(final Map<String, String> expectedRecord) {
        String pickUpCustomNo = expectedRecord.get("Pickup Customer Number");
        String accountId = null;
        String pickUpId = null;

        if (pickUpCustomNo != null) {
            pickUpId = extractFromResponse("Id", getPickUpDetails(pickUpCustomNo));
            accountId = extractFromResponse("Account__c", getPickUpDetails(pickUpCustomNo));
        }
        CaseUpdateDTO caseObj = new CaseUpdateDTO();
        // For Automation : Facility & RecordTypeID will be Fixed, TestData- it wil be BWI
        caseObj.setNA_Mail_Terminal__c("DGM Baltimore - BWI");
        caseObj.setNA_BOL_Number__c(expectedRecord.get("BOL/DSM Number"));

        caseObj.setAccountId(accountId);
        caseObj.setNA_Pickup_Location__c(pickUpId);

        caseObj.setNA_Hold_Type__c(expectedRecord.get("Hold Type"));
        caseObj.setStatus(expectedRecord.get("Status"));
        caseObj.setQty_Of_Expedited_MAX_items__c(expectedRecord.get("MAX"));
        caseObj.setQty_Of_Expedited_items__c(expectedRecord.get("EXP"));
        caseObj.setQty_Of_Ground_items__c(expectedRecord.get("GND"));
        caseObj.setNA_Qty_Of_International_Items__c(expectedRecord.get("INTL"));
        caseObj.setNA_Total_Pallet_Count_On_Placard__c(expectedRecord.get("Pallets"));

        // Convert the Case object to JSON
        return getJsonDataFromObject(caseObj);
    }

    private int getRandomInteger(final int maxLimit) {
        return (maxLimit <= 1) ? 0 : new Faker().number().numberBetween(0, maxLimit);
    }

    private Response getCaseIDDetail(final String caseNumber, final String token) {
        String command = String.format("SELECT Id FROM Case WHERE CaseNumber='%s'", caseNumber);
        return request.baseUri(baseUrl).basePath(END_POINT_PATH.get(HOLDS))
                .header("Authorization", "Bearer " + token)
                .queryParam("q", command).log().all().get();
    }

    private Response getSFAttachmentDetail(final String caseNumber, final String token) {
        String command = String.format("SELECT (SELECT Id FROM ContentDocumentLinks) FROM Case WHERE Status <> 'Closed' AND CaseNumber = '%s' LIMIT 100", caseNumber);
        return request.baseUri(baseUrl).basePath(END_POINT_PATH.get(HOLDS))
                .header("Authorization", "Bearer " + token)
                //.param("q", command).log().all().get();
                 .queryParam("q", command).log().all().get();
    }

    private Response getCaseDetails(final String caseID, final String token) {
        request = buildRequest(HOLD_CASE_DETAIL, "", token, caseID);
        return request.log().all().get();
    }

    private String extractFromCaseIdResponse(final String field, final Response apiResponse) {
        String fieldValue = null;
        if (apiResponse.getStatusCode() == HTTP_STATUS_OK) {
            String jsonResponse = apiResponse.asString();
            JsonPath jsonPath = new JsonPath(jsonResponse);
            fieldValue = jsonPath.getString(field);
        } else {
            fail("Getting unexpected status code: " + apiResponse.getStatusCode());
        }
        return fieldValue;
    }

    @Override
    public String getValueFromHoldDetailsAPI(final String caseNumber, final String fieldName, @NonNull final APICredentials apiCredentials) {
        String token = getAccessToken(apiCredentials);
        String caseID = extractFromResponse("Id", getCaseIDDetail(caseNumber, token));
        String value;
        if (fieldName.equals("Name")) {
            Response caseResponse = getCaseDetails(caseID.trim(), token);
            String pickupNumber = extractFromCaseIdResponse(Constants.HOLDS_API_FIELDS.get("Pickup Customer Number"), caseResponse);
            if (pickupNumber != null) {
                Response pickupResponse = getPickUpDetails(pickupNumber);
                value = extractFromResponse(fieldName, pickupResponse);
                } else {
                value = "-";
            }
        } else {
            value = extractFromCaseIdResponse(fieldName, getCaseDetails(caseID.trim(), token));
        }
        return value == null ? "-" : value;
    }

    @Override
    public String getSFAttachmentCount(final String caseNumber, @NonNull final APICredentials apiCredentials){
        String token = getAccessToken(apiCredentials);
        return extractFromResponse("ContentDocumentLinks.totalSize", getSFAttachmentDetail(caseNumber, token));
    }
}