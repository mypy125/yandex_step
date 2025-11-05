package com.mygitgor.auth_service.service.impl;

import com.mygitgor.auth_service.client.CartClient;
import com.mygitgor.auth_service.client.SellerClient;
import com.mygitgor.auth_service.client.UserClient;
import com.mygitgor.auth_service.domain.BlacklistedToken;
import com.mygitgor.auth_service.dto.*;
import com.mygitgor.auth_service.dto.login.LoginRequest;
import com.mygitgor.auth_service.dto.login.SignupRequest;
import com.mygitgor.auth_service.dto.response.AuthResponse;
import com.mygitgor.auth_service.dto.seller.SellerDto;
import com.mygitgor.auth_service.dto.user.UserDto;
import com.mygitgor.auth_service.dto.user.UserInfo;
import com.mygitgor.auth_service.jwt.JwtProvider;
import com.mygitgor.auth_service.repository.BlacklistedTokenRepository;
import com.mygitgor.auth_service.service.AuthService;
import com.mygitgor.auth_service.service.VerificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final JwtProvider jwtProvider;
    private final UserClient userClient;
    private final CartClient cartClient;
    private final SellerClient sellerClient;
    private final VerificationService verificationService;
    private final TokenCacheService tokenCacheService;
    private final BlacklistedTokenRepository tokenRepository;


    @Override
    public Mono<AuthResponse> login(LoginRequest request) {
        return verificationService.validateOtp(request.getEmail(), request.getOtp(), "LOGIN")
                .flatMap(valid -> {
                    if (!valid) {
                        return Mono.error(new RuntimeException("Invalid OTP"));
                    }
                    return determineUserRoleAndGenerateAuth(request.getEmail());
                });
    }

    private Mono<AuthResponse> determineUserRoleAndGenerateAuth(String email) {
        return sellerClient.existsByEmail(email)
                .flatMap(isSeller -> {
                    if (Boolean.TRUE.equals(isSeller)) {
                        log.info("User identified as SELLER: {}", email);
                        return sellerClient.getAuthInfo(email)
                                .map(sellerInfo -> createAuthResponse(sellerInfo.getEmail(), USER_ROLE.ROLE_SELLER));
                    } else {
                        return userClient.existsByEmail(email)
                                .flatMap(isUser -> {
                                    if (Boolean.TRUE.equals(isUser)) {
                                        log.info("User identified as CUSTOMER: {}", email);
                                        return userClient.getAuthInfo(email)
                                                .map(userInfo -> createAuthResponse(userInfo.getEmail(), USER_ROLE.ROLE_CUSTOMER));
                                    } else {
                                        log.error("User not found: {}", email);
                                        return Mono.error(new RuntimeException("User not found"));
                                    }
                                });
                    }
                })
                .onErrorResume(e -> {
                    log.error("Error determining user role for: {}", email, e);
                    return Mono.error(new RuntimeException("Authentication failed"));
                });
    }

    @Override
    public Mono<Void> requestLoginOtp(String email, USER_ROLE role) {
        return verificationService.sendOtp(email, role, "LOGIN")
                .then()
                .doOnSuccess(v -> log.info("Login OTP sent to: {}", email))
                .doOnError(e -> log.error("Failed to send login OTP to: {}", email, e));
    }

    private Mono<AuthResponse> generateAuthResponse(String email, USER_ROLE role) {
        if (role == USER_ROLE.ROLE_CUSTOMER) {
            return userClient.getAuthInfo(email)
                    .map(userInfo -> createAuthResponse(userInfo.getEmail(), role));
        } else if (role == USER_ROLE.ROLE_SELLER) {
            return sellerClient.getAuthInfo(email)
                    .map(sellerInfo -> createAuthResponse(sellerInfo.getEmail(), role));
        }
        return Mono.error(new RuntimeException("Unsupported role: " + role));
    }

    private AuthResponse createAuthResponse(String email, USER_ROLE role) {
        if (email == null) {
            log.error("Email is null when creating auth response for role: {}", role);
            throw new IllegalArgumentException("Email cannot be null");
        }
        String token = jwtProvider.generateToken(email, role);

        cacheActiveToken(token, email, role);

        return AuthResponse.builder()
                .jwt(token)
                .message("Login success")
                .role(role)
                .email(email)
                .timestamp(LocalDateTime.now())
                .build();
    }

    @Override
    public Mono<AuthResponse> registerUser(SignupRequest request, USER_ROLE role) {
        return verificationService.validateOtp(request.getEmail(), request.getOtp(), "REGISTRATION")
                .flatMap(valid -> {
                    if (!valid) {
                        return Mono.error(new RuntimeException("Invalid OTP"));
                    }

                    if (role == USER_ROLE.ROLE_CUSTOMER) {
                        return registerCustomer(request);
                    }
                    return Mono.error(new RuntimeException("Unsupported role: " + role));
                });
    }

    private Mono<AuthResponse> registerCustomer(SignupRequest request) {
        UserDto userRequest = new UserDto();
        userRequest.setEmail(request.getEmail());
        userRequest.setFullName(request.getFullName());
        userRequest.setOtp(request.getOtp());

        return userClient.createUser(userRequest)
                .flatMap(createdUser -> {
                    if (createdUser.getId() == null) {
                        log.error("User created but ID is null for: {}", request.getEmail());
                        return Mono.error(new RuntimeException("User creation failed - no ID returned"));
                    }

                    String userId = createdUser.getId().toString();
                    log.info("User created with ID: {}, creating cart...", userId);

                    return cartClient.createCart(userId)
                            .then(generateAuthResponse(request.getEmail(), USER_ROLE.ROLE_CUSTOMER));
                })
                .doOnSuccess(response -> log.info("Registration completed: {}", request.getEmail()))
                .doOnError(error -> log.error("Registration failed: {}", request.getEmail(), error));
    }

    @Override
    public Mono<AuthResponse> registerSeller(SellerDto request) {
        return verificationService.sendOtp(request.getEmail(), USER_ROLE.ROLE_SELLER, "REGISTRATION")
                .flatMap(verificationCode -> {
                    return sellerClient.createSeller(request)
                            .then(generateAuthResponse(request.getEmail(), USER_ROLE.ROLE_SELLER));
                });
    }

    @Override
    public Mono<Void> logout(String token) {
        return Mono.fromRunnable(() -> {
            try {
                Date expirationDate = jwtProvider.extractExpiration(token);
                LocalDateTime expiresAt = expirationDate.toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime();

                tokenCacheService.blacklistToken(token, expiresAt);

                saveBlacklistedTokenToDatabase(token, expiresAt);

                log.info("Token blacklisted successfully for logout");
            } catch (Exception e) {
                log.error("Error during logout", e);
                throw new RuntimeException("Logout failed");
            }
        });
    }

    @Override
    public Mono<Boolean> validateToken(String token) {
        return Mono.fromCallable(() -> {
            if (!jwtProvider.validateToken(token)) {
                return false;
            }

            return !isTokenBlacklisted(token);
        }).onErrorResume(error -> {
            log.error("Error validating token", error);
            return Mono.just(false);
        });
    }

    @Override
    public Mono<AuthResponse> verifyAndCompleteSellerRegistration(String email, String otp) {
        return verificationService.validateOtp(email, otp, "REGISTRATION")
                .flatMap(valid -> {
                    if (!valid) {
                        return Mono.error(new RuntimeException("Invalid OTP for seller registration"));
                    }

                    return sellerClient.verifyEmail(email)
                            .then(generateAuthResponse(email, USER_ROLE.ROLE_SELLER))
                            .doOnSuccess(response ->
                                    log.info("Seller registration completed and email verified: {}", email)
                            );
                });
    }

    @Override
    public Mono<UserInfo> getUserInfoFromToken(String token) {
        return Mono.fromCallable(() -> {
            if (Boolean.FALSE.equals(validateToken(token).block())) {
                throw new RuntimeException("Invalid token");
            }

            String email = jwtProvider.getEmailFromJwtToken(token);
            USER_ROLE role = jwtProvider.getRoleFromJwtToken(token);

            UserInfo userInfo = new UserInfo();
            userInfo.setEmail(email);
            userInfo.setRole(role);

            return userInfo;
        });
    }

    private void cacheActiveToken(String token, String email, USER_ROLE role) {
        try {
            String activeTokenKey = "active_token:" + email;

            Date expirationDate = jwtProvider.extractExpiration(token);
            long ttl = (expirationDate.getTime() - System.currentTimeMillis()) / 1000;

            if (ttl > 0) {
                Map<String, Object> tokenInfo = new HashMap<>();
                tokenInfo.put("token", token);
                tokenInfo.put("role", role.name());
                tokenInfo.put("createdAt", LocalDateTime.now());

                tokenCacheService.getRedisTemplate().opsForValue()
                        .set(activeTokenKey, tokenInfo, ttl, TimeUnit.SECONDS);
            }
        } catch (Exception e) {
            log.warn("Failed to cache active token for: {}", email, e);
        }
    }

    private void saveBlacklistedTokenToDatabase(String token, LocalDateTime expiresAt) {
        try {
            BlacklistedToken blacklistedToken = new BlacklistedToken();
            blacklistedToken.setToken(token);
            blacklistedToken.setExpiresAt(expiresAt);
            blacklistedToken.setBlacklistedAt(LocalDateTime.now());

            tokenRepository.save(blacklistedToken);
            log.debug("Blacklisted token saved to database");
        } catch (Exception e) {
            log.error("Failed to save blacklisted token to database", e);
        }
    }

    private boolean isTokenBlacklisted(String token) {
        if (tokenCacheService.isTokenBlacklisted(token)) {
            return true;
        }

        return tokenRepository.existsByToken(token);
    }

    @Scheduled(cron = "0 0 2 * * *")
    @Transactional
    public void cleanupExpiredBlacklistedTokens() {
        try {
            tokenRepository.deleteByExpiresAtBefore(LocalDateTime.now());
            log.info("Expired blacklisted tokens cleaned up from database");
        } catch (Exception e) {
            log.error("Error cleaning up expired blacklisted tokens from database", e);
        }
    }

    @Override
    public Mono<Void> logoutAllDevices(String email) {
        return Mono.fromRunnable(() -> {
            try {
                String activeTokenKey = "active_token:" + email;
                Object cachedToken = tokenCacheService.getRedisTemplate().opsForValue().get(activeTokenKey);

                if (cachedToken != null) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> tokenInfo = (Map<String, Object>) cachedToken;
                    String token = (String) tokenInfo.get("token");

                    Date expirationDate = jwtProvider.extractExpiration(token);
                    LocalDateTime expiresAt = expirationDate.toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDateTime();

                    tokenCacheService.blacklistToken(token, expiresAt);
                    saveBlacklistedTokenToDatabase(token, expiresAt);
                }

                tokenCacheService.getRedisTemplate().delete(activeTokenKey);

                log.info("Logged out from all devices for user: {}", email);
            } catch (Exception e) {
                log.error("Error logging out from all devices for user: {}", email, e);
                throw new RuntimeException("Logout from all devices failed");
            }
        });
    }
}
