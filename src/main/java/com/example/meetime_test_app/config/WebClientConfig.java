package com.example.meetime_test_app.config;

import com.example.meetime_test_app.utils.OAuth2TokenHolder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Configuration
public class WebClientConfig {

    @Value("${integration.hubspot.api.url}")
    public String apiUrl;

    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .baseUrl(apiUrl)
                .filter(authHeaderFilter())
                .build();
    }

    private ExchangeFilterFunction authHeaderFilter() {
        return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
            String accessToken = OAuth2TokenHolder.getToken();
            ClientRequest modifiedRequest = ClientRequest.from(clientRequest)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                    .build();
            return Mono.just(modifiedRequest);
        });
    }
}
