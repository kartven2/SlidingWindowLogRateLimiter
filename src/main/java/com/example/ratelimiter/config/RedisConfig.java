package com.example.ratelimiter.config;

import com.example.ratelimiter.core.RateLimiter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.time.Duration;

@Configuration
public class RedisConfig {

    private static final int MAX_TOTAL_CONNECTIONS = 128;
    private static final int MAX_IDLE_CONNECTIONS = 128;
    private static final int MIN_IDLE_CONNECTIONS = 16;
    private static final int WINDOW_SIZE_SECONDS = 60;
    private static final long MAX_REQUESTS = 2;

    @Value("${REDIS_HOST:localhost}")
    private String redisHost;

    @Value("${REDIS_PORT:6379}")
    private int redisPort;

    @Bean(destroyMethod = "close")
    public JedisPool jedisPool() {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(MAX_TOTAL_CONNECTIONS);
        poolConfig.setMaxIdle(MAX_IDLE_CONNECTIONS);
        poolConfig.setMinIdle(MIN_IDLE_CONNECTIONS);
        return new JedisPool(poolConfig, redisHost, redisPort);
    }

    @Bean
    public RateLimiter rateLimiter(JedisPool jedisPool) {
        return new RateLimiter(jedisPool, MAX_REQUESTS, Duration.ofSeconds(WINDOW_SIZE_SECONDS));
    }
}
