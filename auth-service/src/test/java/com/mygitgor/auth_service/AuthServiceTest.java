package com.mygitgor.auth_service;

import com.mygitgor.auth_service.client.CartClient;
import com.mygitgor.auth_service.client.UserClient;
import com.mygitgor.auth_service.dto.USER_ROLE;
import com.mygitgor.auth_service.dto.login.SignupRequest;
import com.mygitgor.auth_service.jwt.JwtProvider;
import com.mygitgor.auth_service.service.VerificationService;
import com.mygitgor.auth_service.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {
    @Mock
    private UserClient userClient;

    @Mock
    private CartClient cartClient;

    @Mock
    private VerificationService verificationService;

    @Mock
    private JwtProvider jwtProvider;

    @InjectMocks
    private AuthServiceImpl authService;

    private SignupRequest signupRequest;

    @BeforeEach
    void setup() {
        signupRequest = new SignupRequest();
        signupRequest.setEmail("test@example.com");
        signupRequest.setFullName("Test User");
        signupRequest.setOtp("123456");
    }

    @Test
    void registerCustomer_success() {
        Mockito.when(verificationService.validateOtp("test@example.com", "123456", "REGISTRATION"))
                .thenReturn(Mono.just(true));

        Mockito.when(userClient.createUser(Mockito.any()))
                .thenReturn(Mono.empty());

        Mockito.when(cartClient.createCart("test@example.com"))
                .thenReturn(Mono.empty());

        Mockito.when(jwtProvider.generateToken("test@example.com", USER_ROLE.ROLE_CUSTOMER))
                .thenReturn("mock-jwt-token");

        StepVerifier.create(authService.registerUser(signupRequest, USER_ROLE.ROLE_CUSTOMER))
                .assertNext(authResponse -> {
                    assertEquals("mock-jwt-token", authResponse.getJwt());
                    assertEquals(USER_ROLE.ROLE_CUSTOMER, authResponse.getRole());
                    assertEquals("test@example.com", authResponse.getEmail());
                })
                .verifyComplete();
    }

    @Test
    void registerCustomer_invalidOtp() {
        Mockito.when(verificationService.validateOtp("test@example.com", "123456", "REGISTRATION"))
                .thenReturn(Mono.just(false));

        StepVerifier.create(authService.registerUser(signupRequest, USER_ROLE.ROLE_CUSTOMER))
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException
                        && throwable.getMessage().equals("Invalid OTP"))
                .verify();
    }

    @Test
    void registerCustomer_unsupportedRole() {
        Mockito.when(verificationService.validateOtp("test@example.com", "123456", "REGISTRATION"))
                .thenReturn(Mono.just(true));

        StepVerifier.create(authService.registerUser(signupRequest, USER_ROLE.ROLE_SELLER))
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException
                        && throwable.getMessage().contains("Unsupported role"))
                .verify();
    }



}
