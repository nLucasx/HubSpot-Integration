package com.example.meetime_test_app.interceptor;

import com.example.meetime_test_app.dto.response.TokenValidationResponse;
import com.example.meetime_test_app.utils.OAuth2TokenHolder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.HandlerInterceptor;
import reactor.core.publisher.Mono;

public class OAuth2TokenInterceptor implements HandlerInterceptor {

    private final WebClient webClient;

    public OAuth2TokenInterceptor(String apiUrl) {
        this.webClient = WebClient.builder().baseUrl(apiUrl).build();
    }

    @Override
    public boolean preHandle(HttpServletRequest request, @NonNull HttpServletResponse response, @Nullable Object handler) throws Exception {
        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid Token");
            return false;
        }

        String accessToken = authorizationHeader.substring(7);

        if (!isTokenValid(accessToken)) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid Token");
            return false;
        }

        OAuth2TokenHolder.setToken(accessToken);
        return true;
    }

    @Override
    public void afterCompletion(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @Nullable Object handler, @Nullable Exception ex) {
        OAuth2TokenHolder.clear();
    }

    private boolean isTokenValid(String accessToken) {
        String userInfoUrl = "/oauth/v1/access-tokens/" + accessToken;

        try {
            TokenValidationResponse tokenResponse = webClient.get()
                    .uri(userInfoUrl)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, clientResponse -> clientResponse.bodyToMono(Throwable.class))
                    .bodyToMono(TokenValidationResponse.class)
                    .block();

            return tokenResponse != null && tokenResponse.getUserId() != null;
        } catch (Exception e) {
            return false;
        }
    }
}