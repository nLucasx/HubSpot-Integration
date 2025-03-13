package com.example.meetime_test_app.aspect;

import com.example.meetime_test_app.annotation.RateLimited;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Aspect
@Component
public class RateLimitAspect {

    @Autowired
    private Bucket rateLimitBucket;

    @Before("@annotation(rateLimited)")
    public void checkRateLimit(RateLimited rateLimited) throws Throwable {
        ConsumptionProbe probe = rateLimitBucket.tryConsumeAndReturnRemaining(1);

        if (!probe.isConsumed()) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS,"Rate limit exceeded, try again later.");
        }
    }
}