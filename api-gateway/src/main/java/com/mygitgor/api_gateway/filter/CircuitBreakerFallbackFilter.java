package com.mygitgor.api_gateway.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mygitgor.api_gateway.dto.ErrorResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Instant;

@Slf4j
@Component
@RequiredArgsConstructor
public class CircuitBreakerFallbackFilter extends AbstractGatewayFilterFactory<CircuitBreakerFallbackFilter.Config> {
    private final ObjectMapper objectMapper;

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            return chain.filter(exchange)
                    .onErrorResume(throwable -> {
                        log.error("Circuit breaker triggered for route: {}", exchange.getRequest().getPath(), throwable);
                        return handleFallback(exchange, config);
                    });
        };
    }

    private Mono<Void> handleFallback(ServerWebExchange exchange, Config config) {
        exchange.getResponse().setStatusCode(HttpStatus.SERVICE_UNAVAILABLE);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        ErrorResponse errorResponse = new ErrorResponse(
                "SERVICE_UNAVAILABLE",
                config.getMessage() != null ? config.getMessage() : "Service is temporarily unavailable",
                Instant.now().toString(),
                exchange.getRequest().getPath().value()
        );

        try {
            String responseBody = objectMapper.writeValueAsString(errorResponse);
            DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(responseBody.getBytes());
            return exchange.getResponse().writeWith(Mono.just(buffer));
        } catch (JsonProcessingException e) {
            log.error("Error serializing fallback response", e);
            String fallbackJson = "{\"error\":\"SERVICE_UNAVAILABLE\",\"message\":\"Service unavailable\"}";
            DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(fallbackJson.getBytes());
            return exchange.getResponse().writeWith(Mono.just(buffer));
        }
    }

    public static class Config {
        private String message;

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

    @Override
    public Class<Config> getConfigClass() {
        return Config.class;
    }
}