package com.example.ratelimiter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RateLimiterApp{

    public static void main(String[] args) {
        // Starts the Spring Boot application and the embedded web server
        SpringApplication.run(RateLimiterApp.class, args);
    }
}
