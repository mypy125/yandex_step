package com.mygitgor.api_gateway.config;

import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.List;

@Configuration
public class RateLimitConfig {

    @Bean
    public RedisRateLimiter redisRateLimiter(ReactiveRedisTemplate<String, String> redisTemplate,
                                             RedisScript<List<Long>> rateLimitScript) {
        return new RedisRateLimiter(redisTemplate, rateLimitScript);
    }

    @Bean
    public ReactiveRedisTemplate<String, String> reactiveRedisTemplate(
            ReactiveRedisConnectionFactory factory) {
        StringRedisSerializer keySerializer = new StringRedisSerializer();
        RedisSerializer<String> valueSerializer = new StringRedisSerializer();

        RedisSerializationContext<String, String> serializationContext =
                RedisSerializationContext.<String, String>newSerializationContext()
                        .key(keySerializer)
                        .value(valueSerializer)
                        .hashKey(keySerializer)
                        .hashValue(valueSerializer)
                        .build();

        return new ReactiveRedisTemplate<>(factory, serializationContext);
    }

    @Bean
    @SuppressWarnings("unchecked")
    public RedisScript<List<Long>> rateLimitScript() {
        String script = """
            local tokens_key = KEYS[1]
            local timestamp_key = KEYS[2]
            
            local rate = tonumber(ARGV[1])
            local capacity = tonumber(ARGV[2])
            local now = tonumber(ARGV[3])
            local requested = tonumber(ARGV[4])
            
            local fill_time = capacity / rate
            local ttl = math.floor(fill_time * 2)
            
            local last_tokens = tonumber(redis.call("get", tokens_key))
            if last_tokens == nil then
                last_tokens = capacity
            end
            
            local last_refreshed = tonumber(redis.call("get", timestamp_key))
            if last_refreshed == nil then
                last_refreshed = now
            end
            
            local delta = math.max(0, now - last_refreshed)
            local filled_tokens = math.min(capacity, last_tokens + (delta * rate))
            local allowed = filled_tokens >= requested
            local new_tokens = filled_tokens
            local allowed_num = 0
            
            if allowed then
                new_tokens = filled_tokens - requested
                allowed_num = 1
            end
            
            redis.call("setex", tokens_key, ttl, new_tokens)
            redis.call("setex", timestamp_key, ttl, now)
            
            return { allowed_num, new_tokens }
            """;

        return RedisScript.of(script, List.class);
    }
}
