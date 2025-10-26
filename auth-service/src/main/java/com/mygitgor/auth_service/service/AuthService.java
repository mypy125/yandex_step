package com.mygitgor.auth_service.service;

public interface AuthService {
    void sendLoginOtp(String email);
    String verifyOtpAndLogin(String email, String otp);
}
