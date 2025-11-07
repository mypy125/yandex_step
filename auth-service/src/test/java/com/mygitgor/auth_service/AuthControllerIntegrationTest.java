package com.mygitgor.auth_service;

import com.mygitgor.auth_service.client.CartClient;
import com.mygitgor.auth_service.client.UserClient;
import com.mygitgor.auth_service.dto.USER_ROLE;
import com.mygitgor.auth_service.dto.user.UserAuthInfo;
import com.mygitgor.auth_service.dto.user.UserDto;
import com.mygitgor.auth_service.jwt.JwtProvider;
import com.mygitgor.auth_service.service.VerificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static org.hamcrest.Matchers.containsString;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class AuthControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private VerificationService verificationService;

    @MockitoBean
    private UserClient userClient;

    @MockitoBean
    private CartClient cartClient;

    @MockitoBean
    private JwtProvider jwtProvider;

    @BeforeEach
    void setup() {
        UUID mockUserId = UUID.randomUUID();
        UserDto mockUserResponse = UserDto.builder()
                .id(mockUserId)
                .email("test@example.com")
                .fullName("Test User")
                .role(USER_ROLE.ROLE_CUSTOMER)
                .build();

        UserAuthInfo mockAuthInfo = new UserAuthInfo();
        mockAuthInfo.setId(mockUserId.toString());
        mockAuthInfo.setEmail("test@example.com");
        mockAuthInfo.setFullName("Test User");
        mockAuthInfo.setRole(USER_ROLE.ROLE_CUSTOMER);
        mockAuthInfo.setEmailVerified(true);

        Mockito.when(verificationService.validateOtp("test@example.com", "123456", "REGISTRATION"))
                .thenReturn(Mono.just(true));

        Mockito.when(userClient.createUser(Mockito.any(UserDto.class)))
                .thenReturn(Mono.just(mockUserResponse));

        Mockito.when(userClient.getAuthInfo("test@example.com"))
                .thenReturn(Mono.just(mockAuthInfo));

        Mockito.when(cartClient.createCart(mockUserId.toString()))
                .thenReturn(Mono.empty());

        Mockito.when(jwtProvider.generateToken("test@example.com", USER_ROLE.ROLE_CUSTOMER))
                .thenReturn("mock-jwt-token");
    }

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
                .jsonPath("$.jwt").isEqualTo("mock-jwt-token")
                .jsonPath("$.email").isEqualTo("test@example.com")
                .jsonPath("$.role").isEqualTo("ROLE_CUSTOMER")
                .jsonPath("$.message").isNotEmpty();
    }

    @Test
    void registerUserFlow_invalidOtp() {
        Mockito.when(verificationService.validateOtp("test@example.com", "123456", "REGISTRATION"))
                .thenReturn(Mono.just(false));

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
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.message").value(containsString("Invalid OTP"));
    }
}
