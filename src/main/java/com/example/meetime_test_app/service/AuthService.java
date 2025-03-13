package com.example.meetime_test_app.service;

import com.example.meetime_test_app.builder.AuthRequestBuilder;
import com.example.meetime_test_app.dto.response.AuthResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

@Service
public class AuthService {

    @Value("${oauth.hubspot.client-id}")
    public String clientId;

    @Value("${oauth.hubspot.client-secret}")
    public String clientSecret;

    @Value("${oauth.hubspot.redirect-uri}")
    public String redirectUri;

    @Autowired
    private WebClient webClient;

    public String endpoint = "/oauth/v1/token";

    public Mono<AuthResponse> authenticate(String code) {
        MultiValueMap<String, String> body = AuthRequestBuilder.buildAuthenticate(code, clientId, clientSecret, redirectUri);

        return webClient.post()
                .uri(endpoint)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .bodyValue(body)
                .retrieve()
                .onStatus(HttpStatusCode::isError, response ->
                        Mono.error(new ResponseStatusException(response.statusCode(), "Invalid code"))
                )
                .bodyToMono(AuthResponse.class);

    }
}
