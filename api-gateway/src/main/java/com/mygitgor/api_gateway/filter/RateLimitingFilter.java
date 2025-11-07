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

@Slf4j
@Component
@RequiredArgsConstructor
public class RateLimitingFilter extends AbstractGatewayFilterFactory<RateLimitingFilter.Config> {

    private final ReactiveRedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;
    private final RedisScript<List<Long>> rateLimitScript;

    public RateLimitingFilter() {
        super(Config.class);
        this.redisTemplate = null;
        this.objectMapper = new ObjectMapper();
        this.rateLimitScript = null;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            log.debug("Rate limiting check - replenishRate: {}, burstCapacity: {}",
                    config.getReplenishRate(), config.getBurstCapacity());

            return chain.filter(exchange);
        };
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