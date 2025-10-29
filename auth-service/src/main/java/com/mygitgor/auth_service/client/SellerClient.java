package com.mygitgor.auth_service.client;

import com.mygitgor.auth_service.dto.seller.SellerAuthInfo;
import com.mygitgor.auth_service.dto.seller.SellerCreateRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class SellerClient {
    private final WebClient.Builder webClientBuilder;

    @Value("${user.service.url:http://user-management-service:8083/api/sellers}")
    private String baseUrl;

    public Mono<Boolean> existsByEmail(String email) {
        return webClientBuilder.build()
                .get()
                .uri(baseUrl + "/exists/{email}", email)
                .retrieve()
                .bodyToMono(Boolean.class)
                .onErrorReturn(false);
    }

    public Mono<SellerAuthInfo> getAuthInfo(String email) {
        return webClientBuilder.build()
                .get()
                .uri(baseUrl + "/{email}/auth-info", email)
                .retrieve()
                .bodyToMono(SellerAuthInfo.class)
                .onErrorResume(e -> {
                    log.error("Error fetching seller auth info for email: {}", email, e);
                    return Mono.error(new RuntimeException("Seller not found"));
                });
    }

    public Mono<Void> createSeller(SellerCreateRequest request) {
        return webClientBuilder.build()
                .post()
                .uri(baseUrl)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(Void.class)
                .onErrorResume(e -> {
                    log.error("Error creating seller: {}", request.getEmail(), e);
                    return Mono.error(new RuntimeException("Failed to create seller"));
                });
    }

    public Mono<Boolean> verifyEmail(String email) {
        return webClientBuilder.build()
                .patch()
                .uri(baseUrl + "/{email}/verify", email)
                .retrieve()
                .bodyToMono(Boolean.class)
                .onErrorResume(e -> {
                    log.error("Error verifying seller email: {}", email, e);
                    return Mono.error(new RuntimeException("Failed to verify seller email"));
                });
    }
}
