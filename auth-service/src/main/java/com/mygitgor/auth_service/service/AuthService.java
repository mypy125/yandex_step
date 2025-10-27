package com.mygitgor.auth_service.service;

import com.mygitgor.auth_service.dto.SignupRequest;

public interface AuthService {
    void sendLoginOtp(String email);
    String verifyOtpAndLogin(String email, String otp);
    String createUser(SignupRequest req);
}
