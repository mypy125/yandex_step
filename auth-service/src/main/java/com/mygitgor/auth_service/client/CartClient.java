package com.mygitgor.auth_service.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class CartClient {
    private final WebClient.Builder webClientBuilder;

    @Value("${cart.service.url:http://localhost:8084/api/carts}")
    private String baseUrl;

    public Mono<Void> createCart(String email) {
        return webClientBuilder.build()
                .post()
                .uri(baseUrl + "/create/{email}", email)
                .retrieve()
                .bodyToMono(Void.class)
                .onErrorResume(e -> {
                    log.error("Error creating cart for user: {}", email, e);
                    return Mono.error(new RuntimeException("Failed to create cart"));
                });
    }
}
