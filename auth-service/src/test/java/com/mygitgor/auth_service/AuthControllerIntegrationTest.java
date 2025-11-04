package com.mygitgor.auth_service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class AuthControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void registerUserFlow() {
        webTestClient.post()
                .uri("/auth/register?role=ROLE_CUSTOMER")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                {
                  "email": "test@example.com",
                  "fullName": "Test User",
                  "otp": "123456"
                }
            """)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.jwt").isNotEmpty()
                .jsonPath("$.email").isEqualTo("test@example.com");
    }
}
