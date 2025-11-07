package com.mygitgor.api_gateway.controller;

import com.mygitgor.api_gateway.dto.ErrorResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/gateway")
@RequiredArgsConstructor
public class FallbackController {

    @GetMapping("/fallback")
    public Mono<ResponseEntity<ErrorResponse>> fallback(ServerWebExchange exchange) {
        String path = exchange.getRequest().getPath().value();

        ErrorResponse errorResponse = new ErrorResponse(
                "SERVICE_UNAVAILABLE",
                "Service temporarily unavailable. Please try again later.",
                Instant.now().toString(),
                path
        );

        log.warn("Fallback triggered for path: {}", path);

        return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(errorResponse));
    }

    @GetMapping("/health")
    public Mono<ResponseEntity<Map<String, String>>> health() {
        Map<String, String> status = new HashMap<>();
        status.put("status", "UP");
        status.put("service", "api-gateway");
        status.put("timestamp", Instant.now().toString());

        return Mono.just(ResponseEntity.ok(status));
    }

    @GetMapping("/circuit-breaker-fallback")
    public Mono<ResponseEntity<ErrorResponse>> circuitBreakerFallback(ServerWebExchange exchange) {
        String path = exchange.getRequest().getPath().value();

        ErrorResponse errorResponse = new ErrorResponse(
                "CIRCUIT_BREAKER_OPEN",
                "Service is temporarily unavailable due to high load. Please try again later.",
                Instant.now().toString(),
                path
        );

        return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(errorResponse));
    }
}