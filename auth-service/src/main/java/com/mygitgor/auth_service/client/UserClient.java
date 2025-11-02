package com.mygitgor.auth_service.client;

import com.mygitgor.auth_service.dto.user.UserAuthInfo;
import com.mygitgor.auth_service.dto.user.UserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserClient {
    private final WebClient.Builder webClientBuilder;

    @Value("${user.service.url:http://localhost:8082/api/users}")
    private String baseUrl;

    public Mono<Boolean> existsByEmail(String email) {
        return webClientBuilder.build()
                .get()
                .uri(baseUrl + "/exists/{email}", email)
                .retrieve()
                .bodyToMono(Boolean.class)
                .onErrorReturn(false);
    }

    public Mono<UserAuthInfo> getAuthInfo(String email) {
        return webClientBuilder.build()
                .get()
                .uri(baseUrl + "/{email}/auth-info", email)
                .retrieve()
                .bodyToMono(UserAuthInfo.class)
                .onErrorResume(e -> {
                    log.error("Error fetching user auth info for email: {}", email, e);
                    return Mono.error(new RuntimeException("User not found"));
                });
    }

    public Mono<UserDto> createUser(UserDto request) {
        return webClientBuilder.build()
                .post()
                .uri(baseUrl)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(UserDto.class)
                .onErrorResume(e -> {
                    log.error("Error creating user: {}", request.getEmail(), e);
                    return Mono.error(new RuntimeException("Failed to create user"));
                });
    }


}
