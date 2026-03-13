package com.dhl.ecommerce.holdssbe.api;

import com.dhl.ecommerce.holdssbe.model.APICredentials;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import lombok.NonNull;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(Enclosed.class)
public class WireMockAPIClient implements APIClient  {

    private WireMockServer wireMockServer;

    // Initialize WireMock server
    public WireMockAPIClient(WireMockServer wireMockServer) {
        this.wireMockServer = wireMockServer;
        WireMock.configureFor(wireMockServer.port());
    }

    @Override
    public String getAccessToken(APICredentials credentials) {
        // Return a mock token or simulate token generation for WireMock
        wireMockServer.stubFor(post(urlEqualTo("/services/oauth2/token"))
                .withRequestBody(containing("grant_type=password"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{ \"access_token\": \"mock_access_token\" }")));
        return "mock_access_token";
    }

    @Override
    public void createHoldCase(APICredentials credentials) {
        // Simulate creating a hold case with WireMock
        wireMockServer.stubFor(post(urlPathMatching("/services/data/.+/sobjects/Case/"))
                .willReturn(aResponse()
                        .withStatus(201)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{ \"id\": \"mock_case_id\" }")));
        System.out.println("Mocked Create Hold Case response: Case ID = mock_case_id");
    }

    @Override
    public boolean updateHoldCase(String caseID, Map<String, String> expectedRecord, APICredentials credentials) {
        wireMockServer.stubFor(patch(urlPathMatching("/services/data/.+/sobjects/Case/" + caseID))
                .willReturn(aResponse()
                        .withStatus(204)));
        return true;
    }

    @Override
    public boolean closeHoldCase(String caseID, APICredentials credentials) {
        // Simulate closing a hold case with WireMock
        wireMockServer.stubFor(patch(urlPathMatching("/services/data/.+/sobjects/Case/" + caseID))
                .willReturn(aResponse()
                        .withStatus(204)));
        return true;
    }

    @Override
    public String getValueFromHoldDetailsAPI(String caseNumber, String fieldName, APICredentials credentials){
        return null;
    }

    public String getSFAttachmentCount(final String caseNumber, @NonNull final APICredentials apiCredentials){
        return null;
    }

    public static class WireMockAPIClientTest {
        private WireMockServer wireMockServer;
        private WireMockAPIClient wireMockAPIClient;

        @Before
        public void setUp() {
            wireMockServer = new WireMockServer(8080);  // Start WireMock server on port 8080
            wireMockServer.start();
            wireMockAPIClient = new WireMockAPIClient(wireMockServer);  // Initialize the API client
        }

        @After
        public void tearDown() {
            wireMockServer.stop();  // Stop WireMock server after tests
        }

        @Test
        public void testGetAccessToken() {
            String accessToken = wireMockAPIClient.getAccessToken(new APICredentials("clientId", "clientSecret", "userName", "password"));
            assertEquals("mock_access_token", accessToken);
        }

        @Test
        public void testCreateHoldCase() {
            wireMockAPIClient.createHoldCase(new APICredentials("clientId", "clientSecret", "userName", "password"));
            // You can add assertions to check if the mock case was created successfully
        }

        @Test
        public void testUpdateHoldCase() {
            boolean updateStatus = wireMockAPIClient.updateHoldCase("mock_case_id", null, new APICredentials("clientId", "clientSecret", "userName", "password"));
            assertTrue(updateStatus);
        }

        @Test
        public void testCloseHoldCase() {
            boolean closeStatus = wireMockAPIClient.closeHoldCase("mock_case_id", new APICredentials("clientId", "clientSecret", "userName", "password"));
            assertTrue(closeStatus);
        }
    }

}
