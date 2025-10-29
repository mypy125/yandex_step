package com.mygitgor.auth_service.service.impl;

import com.mygitgor.auth_service.client.SellerClient;
import com.mygitgor.auth_service.client.UserClient;
import com.mygitgor.auth_service.domain.BlacklistedToken;
import com.mygitgor.auth_service.dto.*;
import com.mygitgor.auth_service.dto.response.AuthResponse;
import com.mygitgor.auth_service.dto.seller.SellerCreateRequest;
import com.mygitgor.auth_service.dto.user.UserCreateRequest;
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
    private final SellerClient sellerClient;
    private final VerificationService verificationService;
    private final TokenCacheService tokenCacheService;
    private final BlacklistedTokenRepository tokenRepository;


    @Override
    public Mono<AuthResponse> login(LoginRequest request, USER_ROLE role) {
        return verificationService.validateOtp(request.getEmail(), request.getOtp(), "LOGIN")
                .flatMap(valid -> {
                    if (!valid) {
                        return Mono.error(new RuntimeException("Invalid OTP"));
                    }
                    return generateAuthResponse(request.getEmail(), role);
                });
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
        String token = jwtProvider.generateToken(email, role);

        cacheActiveToken(token, email, role);

        AuthResponse authResponse = new AuthResponse();
        authResponse.setJwt(token);
        authResponse.setMessage("Login success");
        authResponse.setRole(role);

        return authResponse;
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
                    } else if (role == USER_ROLE.ROLE_SELLER) {
                        return registerSeller(request);
                    }
                    return Mono.error(new RuntimeException("Unsupported role: " + role));
                });
    }

    private Mono<AuthResponse> registerCustomer(SignupRequest request) {
        UserCreateRequest userRequest = new UserCreateRequest();
        userRequest.setEmail(request.getEmail());
        userRequest.setFullName(request.getFullName());
        userRequest.setOtp(request.getOtp());
        userRequest.setMobile("37444******");

        return userClient.createUser(userRequest)
                .then(userClient.createCart(request.getEmail()))
                .then(generateAuthResponse(request.getEmail(), USER_ROLE.ROLE_CUSTOMER));
    }

    private Mono<AuthResponse> registerSeller(SignupRequest request) {
        SellerCreateRequest sellerRequest = new SellerCreateRequest();
        sellerRequest.setEmail(request.getEmail());
        sellerRequest.setFullName(request.getFullName());
        sellerRequest.setOtp(request.getOtp());

        return verificationService.sendOtp(request.getEmail(), USER_ROLE.ROLE_SELLER, "REGISTRATION")
                .flatMap(verificationCode -> {
                    return sellerClient.createSeller(sellerRequest)
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
