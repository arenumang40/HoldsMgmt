package com.dhl.ecommerce.holdssbe.model;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class APICredentials {
    private String clientId;
    private String clientSecret;
    private String userName;
    private String password;

    public APICredentials(final String clientId, final String clientSecret, final String userName, final String password) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.userName = userName;
        this.password = password;
    }

}

