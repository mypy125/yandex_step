package com.mygitgor.api_gateway.config;

import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import reactor.core.publisher.Mono;

import java.util.List;

public class RedisRateLimiter {

    private final ReactiveRedisTemplate<String, String> redisTemplate;
    private final RedisScript<List<Long>> rateLimitScript;

    public RedisRateLimiter(ReactiveRedisTemplate<String, String> redisTemplate,
                            RedisScript<List<Long>> rateLimitScript) {
        this.redisTemplate = redisTemplate;
        this.rateLimitScript = rateLimitScript;
    }

    public Mono<Boolean> isAllowed(String key, int replenishRate, int burstCapacity) {
        long now = System.currentTimeMillis() / 1000;
        List<String> keys = List.of("rate_limiter_tokens:" + key, "rate_limiter_timestamp:" + key);

        return redisTemplate.execute(rateLimitScript, keys,
                        String.valueOf(replenishRate),
                        String.valueOf(burstCapacity),
                        String.valueOf(now),
                        "1")
                .next()
                .map(result -> result != null && result.get(0) == 1L);
    }
}
