package com.mygitgor.auth_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class TokenCacheService {
    private final RedisTemplate<String, Object> redisTemplate;
    private static final String PREFIX = "blacklisted_token:";

    public void blacklistToken(String token, LocalDateTime expiresAt) {
        long ttl = Duration.between(LocalDateTime.now(), expiresAt).getSeconds();
        if (ttl > 0) {
            redisTemplate.opsForValue().set(PREFIX + token, true, ttl, TimeUnit.SECONDS);
        }
    }

    public boolean isTokenBlacklisted(String token) {
        Boolean exists = (Boolean) redisTemplate.opsForValue().get(PREFIX + token);
        return Boolean.TRUE.equals(exists);
    }
}
