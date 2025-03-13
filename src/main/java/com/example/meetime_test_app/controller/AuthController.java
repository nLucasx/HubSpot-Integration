package com.example.meetime_test_app.controller;

import com.example.meetime_test_app.dto.response.AuthResponse;
import com.example.meetime_test_app.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import static org.springframework.http.HttpHeaders.LOCATION;

@Validated
@RestController
@RequestMapping("/oauth")
public class AuthController {
    @Autowired
    private AuthService authService;

    @Value("${oauth.hubspot.url}")
    public String oAuthUrl;

    @GetMapping
    public void startOAuthFlow(HttpServletResponse httpServletResponse) {
        httpServletResponse.setHeader(LOCATION, oAuthUrl);
        httpServletResponse.setStatus(302);
    }

    @GetMapping("/callback")
    public Mono<AuthResponse> oAuthCallback(@RequestParam("code") @NotBlank(message = "Code must not be blank") String code) {
        return this.authService.authenticate(code);
    }
}
