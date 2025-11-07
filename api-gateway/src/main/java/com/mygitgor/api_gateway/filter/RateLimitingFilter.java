package com.mygitgor.api_gateway.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mygitgor.api_gateway.dto.ErrorResponse;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class RateLimitingFilter extends AbstractGatewayFilterFactory<RateLimitingFilter.Config> {
    private final ReactiveRedisTemplate<String, String> redisTemplate;
    private final RedisScript<List<Long>> rateLimitScript;
    private final ObjectMapper objectMapper;

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            String clientIp = getClientIp(exchange);
            String redisKey = "rate_limit:" + clientIp;

            List<String> keys = List.of(redisKey);
            List<String> args = List.of(
                    String.valueOf(config.getReplenishRate()),
                    String.valueOf(config.getBurstCapacity()),
                    String.valueOf(System.currentTimeMillis())
            );

            return redisTemplate.execute(rateLimitScript, keys, args)
                    .next()
                    .flatMap(results -> {
                        Long allowed = results.get(0);
                        Long tokensLeft = results.get(1);

                        if (allowed == 1L) {
                            log.debug("Request allowed for {}. Tokens left: {}", clientIp, tokensLeft);
                            return chain.filter(exchange);
                        } else {
                            log.warn("Rate limit exceeded for {}", clientIp);
                            return handleRateLimitExceeded(exchange, config);
                        }
                    })
                    .onErrorResume(e -> {
                        log.error("Rate limiting error: {}", e.getMessage());
                        return chain.filter(exchange);
                    });
        };
    }

    private String getClientIp(ServerWebExchange exchange) {
        String forwarded = exchange.getRequest().getHeaders().getFirst("X-Forwarded-For");
        return forwarded != null ? forwarded : Objects.requireNonNull(exchange.getRequest().getRemoteAddress()).getAddress().getHostAddress();
    }

    private Mono<Void> handleRateLimitExceeded(ServerWebExchange exchange, Config config) {
        exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        ErrorResponse errorResponse = new ErrorResponse(
                "RATE_LIMIT_EXCEEDED",
                "Too many requests. Please try again later.",
                exchange.getRequest().getPath().value()
        );

        try {
            String responseBody = objectMapper.writeValueAsString(errorResponse);
            DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(responseBody.getBytes());
            return exchange.getResponse().writeWith(Mono.just(buffer));
        } catch (JsonProcessingException e) {
            log.error("Error serializing rate limit error response", e);
            String fallbackResponse = "{\"error\":\"RATE_LIMIT_EXCEEDED\",\"message\":\"Too many requests\"}";
            DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(fallbackResponse.getBytes());
            return exchange.getResponse().writeWith(Mono.just(buffer));
        }
    }

    @Data
    public static class Config {
        private int replenishRate;
        private int burstCapacity;

        public Config() {
            this.replenishRate = 10;
            this.burstCapacity = 20;
        }

        public Config(int replenishRate, int burstCapacity) {
            this.replenishRate = replenishRate;
            this.burstCapacity = burstCapacity;
        }
    }

    @Override
    public List<String> shortcutFieldOrder() {
        return List.of("replenishRate", "burstCapacity");
    }
}