package com.dhl.ecommerce.holdssbe.api;

import com.github.tomakehurst.wiremock.WireMockServer;
import java.util.Locale;

public class APIClientFactory {

    /**
     * Creates the appropriate APIClient instance based on the environment.
     */
    public APIClient generateAPIClient(final String environment) {
        final APIClient client;

        switch (environment.toLowerCase(Locale.ROOT)) {
            case "salesforce":
                client =  new SalesforceAPIClient();
                break;
            case "wiremock":
                final WireMockServer wireMockServer = new WireMockServer(); // Initialize WireMock server here
                client = new WireMockAPIClient(wireMockServer);
                break;
            default:
                throw new IllegalArgumentException("Unsupported environment: " + environment);
        }
        return client;
    }
}
