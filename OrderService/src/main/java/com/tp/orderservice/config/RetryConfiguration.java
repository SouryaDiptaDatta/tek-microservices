package com.tp.orderservice.config;

import io.github.resilience4j.retry.Retry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class RetryConfiguration {

    @Bean
    public Retry userServiceRetry() {

        io.github.resilience4j.retry.RetryConfig config =
                io.github.resilience4j.retry.RetryConfig.custom()
                        .maxAttempts(3)
                        .waitDuration(Duration.ofSeconds(1))
                        .build();

        return Retry.of("userService", config);
    }
}