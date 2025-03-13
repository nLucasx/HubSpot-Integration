package com.example.meetime_test_app.config;

import com.example.meetime_test_app.interceptor.OAuth2TokenInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${integration.hubspot.api.url}")
    public String apiUrl;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new OAuth2TokenInterceptor(apiUrl))
                .addPathPatterns("/contact")
                .excludePathPatterns("/contact/webhook");
    }
}