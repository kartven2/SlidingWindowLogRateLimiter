package com.example.ratelimiter.core;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Response;
import redis.clients.jedis.Transaction;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

public class RateLimiter {
    private final JedisPool jedisPool;
    private final long maxRequests;
    private final Duration windowSize;

    public RateLimiter(JedisPool jedisPool, long maxRequests, Duration windowSize) {
        this.jedisPool = jedisPool;
        this.maxRequests = maxRequests;
        this.windowSize = windowSize;
    }

    public boolean isAllowed(String userId) {
        String key = "rate_limiter:" + userId;
        long now = Instant.now().toEpochMilli();
        long windowStart = now - windowSize.toMillis();

        // Unique member to avoid deduplication if multiple requests hit in the exact same millisecond
        String member = now + ":" + UUID.randomUUID().toString().substring(0, 8);
        long ttlSeconds = windowSize.toSeconds();

        try (Jedis jedis = jedisPool.getResource()) {
            Transaction transaction = jedis.multi();

            // 1. Evict out-of-window requests
            transaction.zremrangeByScore(key, 0, windowStart);

            // 2. Optimistically add current request
            transaction.zadd(key, now, member);

            // 3. Get total entries in the current window
            Response<Long> requestCountResponse = transaction.zcard(key);
            
            // 4. Reset TTL so inactive keys self-destruct and don't leak memory
            transaction.expire(key, ttlSeconds);

            // Execute all commands atomically
            transaction.exec();

            long requestCount = requestCountResponse.get();
            if (requestCount <= maxRequests) {
                return true;
            } else {
                // Rollback the addition of the current request since it exceeds the limit
                jedis.zrem(key, member);
                return false;
            }
        }
    }
}