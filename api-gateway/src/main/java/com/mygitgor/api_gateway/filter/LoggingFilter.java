package com.mygitgor.api_gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@Component
public class LoggingFilter implements GatewayFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        long startTime = System.currentTimeMillis();
        String requestId = UUID.randomUUID().toString();

        var request = exchange.getRequest();

        log.info("Incoming Request: ID={}, Method={}, Path={}, Headers={}",
                requestId, request.getMethod(), request.getPath(),
                getHeadersForLogging(request.getHeaders()));

        var modifiedRequest = request.mutate()
                .header("X-Request-ID", requestId)
                .build();

        return chain.filter(exchange.mutate().request(modifiedRequest).build())
                .doOnSuccess(aVoid -> {
                    long duration = System.currentTimeMillis() - startTime;
                    var response = exchange.getResponse();
                    log.info("Request Completed: ID={}, Status={}, Duration={}ms",
                            requestId, response.getStatusCode(), duration);
                })
                .doOnError(throwable -> {
                    long duration = System.currentTimeMillis() - startTime;
                    log.error("Request Failed: ID={}, Duration={}ms, Error={}",
                            requestId, duration, throwable.getMessage(), throwable);
                });
    }

    private String getHeadersForLogging(org.springframework.http.HttpHeaders headers) {
        return headers.entrySet().stream()
                .filter(entry -> !entry.getKey().toLowerCase().contains("authorization"))
                .map(entry -> entry.getKey() + ": " + entry.getValue())
                .reduce((a, b) -> a + "; " + b)
                .orElse("No headers");
    }
}