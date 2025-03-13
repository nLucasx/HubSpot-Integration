package com.example.meetime_test_app.config;

import io.github.bucket4j.Bucket;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class RateLimitConfig {

    @Bean
    public Bucket rateLimitBucket() {
        return Bucket.builder()
                .addLimit(limit -> limit.capacity(110).refillGreedy(110, Duration.ofSeconds(10)))
                .build();
    }
}
