package com.example.meetime_test_app.builder;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

public class AuthRequestBuilder {

    public static MultiValueMap<String, String>buildAuthenticate(String code, String clientId, String clientSecret, String redirectUri) {
        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("grant_type", "authorization_code");
        requestBody.add("client_id", clientId);
        requestBody.add("client_secret", clientSecret);
        requestBody.add("redirect_uri", redirectUri);
        requestBody.add("code", code);

        return requestBody;
    }

}
