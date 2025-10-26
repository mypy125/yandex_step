package com.mygitgor.auth_service.client;

import com.mygitgor.auth_service.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class UserClient {
    private final WebClient.Builder webClientBuilder;

    private static final String BASE_URL = "http://user-management-service:8082/api/users";

    public void saveVerificationCode(String email, String otp) {
        webClientBuilder.build()
                .post()
                .uri(BASE_URL + "/verification")
                .bodyValue(Map.of("email", email, "otp", otp))
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

    public boolean verifyOtp(String email, String otp) {
        return Boolean.TRUE.equals(
                webClientBuilder.build()
                        .get()
                        .uri(BASE_URL + "/verify?email={email}&otp={otp}", email, otp)
                        .retrieve()
                        .bodyToMono(Boolean.class)
                        .block()
        );
    }

    public UserDto getUserByEmail(String email) {
        return webClientBuilder.build()
                .get()
                .uri(BASE_URL + "/{email}", email)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response -> {
                    throw new RuntimeException("User not found: " + email);
                })
                .onStatus(HttpStatusCode::is5xxServerError, response -> {
                    throw new RuntimeException("User service error");
                })
                .bodyToMono(UserDto.class)
                .block();
    }
}
