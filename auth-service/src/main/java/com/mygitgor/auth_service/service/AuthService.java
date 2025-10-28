package com.mygitgor.auth_service.service;

import com.mygitgor.auth_service.dto.SignupRequest;

import java.util.UUID;

public interface AuthService {
    void sendLoginOtp(String email);
    String verifyOtpAndLogin(String email, String otp);
    String createUser(SignupRequest req);
    void logout(String token, UUID userId);
    boolean isTokenBlacklisted(String token);
}
