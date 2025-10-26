package com.mygitgor.auth_service.service;

import com.mygitgor.auth_service.config.RabbitConfig;
import com.mygitgor.auth_service.client.UserClient;
import com.mygitgor.auth_service.dto.UserDto;
import com.mygitgor.auth_service.jwt.JwtProvider;
import com.mygitgor.auth_service.utils.OtpUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserClient userClient;
    private final JwtProvider jwtProvider;
    private final OtpSenderService otpSenderService;

    @Override
    public void sendLoginOtp(String email) {
        try{
            String otp = OtpUtil.generateOtp();
            userClient.saveVerificationCode(email, otp);
            otpSenderService.sendOtpToNotificationService(email,otp);
            log.info("OTP sent for {}", email);
        }catch (Exception e){
            log.error("Failed to send OTP for {}", email, e);
            throw new RuntimeException("Failed to send OTP: " + e.getMessage());
        }

    }

    @Override
    public String verifyOtpAndLogin(String email, String otp) {
        try {
            boolean valid = userClient.verifyOtp(email, otp);
            if (!valid) {
                throw new RuntimeException("Invalid OTP");
            }
            UserDto user = userClient.getUserByEmail(email);
            String token = jwtProvider.generateToken(user.email(), user.getRoles());
            //TODO: catching token
            log.info("User {} successfully logged in with roles: {}", email, user.getRoles());
            return token;

        } catch (Exception e) {
            log.error("Login failed for {}", email, e);
            throw new RuntimeException("Authentication failed: " + e.getMessage());
        }
    }
}
