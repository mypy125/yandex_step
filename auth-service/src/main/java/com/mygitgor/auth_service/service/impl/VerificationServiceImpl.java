package com.mygitgor.auth_service.service.impl;

import com.mygitgor.auth_service.client.SellerClient;
import com.mygitgor.auth_service.client.UserClient;
import com.mygitgor.auth_service.domain.VerificationCode;
import com.mygitgor.auth_service.dto.USER_ROLE;
import com.mygitgor.auth_service.dto.VerifyOtpRequest;
import com.mygitgor.auth_service.repository.VerificationCodeRepository;
import com.mygitgor.auth_service.service.VerificationService;
import com.mygitgor.auth_service.utils.OtpUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class VerificationServiceImpl implements VerificationService {
    private final VerificationCodeRepository verificationCodeRepository;
    private final NotificationService notificationService;

    @Override
    public Mono<VerificationCode> sendOtp(String email, USER_ROLE role, String purpose) {
        return generateAndSendOtp(email, role, purpose)
                .doOnSuccess(verificationCode ->
                        log.info("OTP generated and sent for email: {}, purpose: {}", email, purpose)
                )
                .doOnError(error ->
                        log.error("Failed to send OTP for email: {}", email, error)
                );
    }

    private Mono<VerificationCode> generateAndSendOtp(String email, USER_ROLE role, String purpose) {
        return Mono.fromCallable(() -> {
            List<VerificationCode> existingCodes = verificationCodeRepository.findByEmailAndPurpose(email, purpose);
            verificationCodeRepository.deleteAll(existingCodes);

            String otp = OtpUtil.generateOtp();

            VerificationCode verificationCode = new VerificationCode();
            verificationCode.setOtp(otp);
            verificationCode.setEmail(email);
            verificationCode.setUserRole(role);
            verificationCode.setPurpose(purpose);

            VerificationCode savedCode = verificationCodeRepository.save(verificationCode);
            log.info("Generated OTP for email: {}, purpose: {}", email, purpose);

            sendOtpEmail(email, otp, purpose, role);

            return savedCode;
        }).onErrorResume(error -> {
            log.error("Failed to generate OTP for email: {}", email, error);
            return Mono.error(new RuntimeException("Failed to generate OTP"));
        });
    }

    private void sendOtpEmail(String email, String otp, String purpose, USER_ROLE role) {
        String subject = getEmailSubject(purpose, role);
        String text = getEmailText(otp, purpose, role);

        notificationService.sendVerificationOtpEmail(email, otp, subject, text)
                .doOnSuccess(unused ->
                        log.info("OTP email sent to notification service for: {}", email)
                )
                .doOnError(error ->
                        log.error("Failed to send OTP email for: {}", email, error)
                )
                .subscribe();
    }

    @Override
    public Mono<Boolean> verifyOtp(String otp, VerifyOtpRequest request) {
        return validateOtp(request.getEmail(), otp, "EMAIL_VERIFICATION")
                .flatMap(valid -> {
                    if (!valid) {
                        return Mono.error(new RuntimeException("Invalid or expired OTP"));
                    }
                    return Mono.just(true);
                });
    }

    @Override
    public Mono<Boolean> validateOtp(String email, String otp, String purpose) {
        return Mono.fromCallable(() -> {
            VerificationCode verificationCode = verificationCodeRepository.findByEmailAndOtpAndPurpose(email, otp, purpose);
            if (verificationCode == null || !verificationCode.isValid()) {
                log.warn("Invalid OTP for email: {}, purpose: {}", email, purpose);
                return false;
            }

            verificationCode.setUsed(true);
            verificationCodeRepository.save(verificationCode);
            log.info("OTP validated successfully for email: {}, purpose: {}", email, purpose);
            return true;
        }).onErrorResume(error -> {
            log.error("Error validating OTP for email: {}", email, error);
            return Mono.just(false);
        });
    }

    private String getEmailSubject(String purpose, USER_ROLE role) {
        switch (purpose.toUpperCase()) {
            case "REGISTRATION":
                return "Verify Your Account - Ecommerce Multivendor";
            case "EMAIL_VERIFICATION":
                return "Verify Your Seller Account - Ecommerce Multivendor";
            case "LOGIN":
                return "Your Login OTP - Ecommerce Multivendor";
            case "PASSWORD_RESET":
                return "Password Reset OTP - Ecommerce Multivendor";
            default:
                return "Your OTP - Ecommerce Multivendor";
        }
    }

    private String getEmailText(String otp, String purpose, USER_ROLE role) {
        switch (purpose.toUpperCase()) {
            case "REGISTRATION":
                return "Welcome! Your registration OTP is: " + otp;
            case "EMAIL_VERIFICATION":
                String verificationLink = "https://ecommerce-multivendor-frontend.onrender.com/verify-seller?otp=" + otp;
                return "Welcome! Verify your seller account: " + verificationLink;
            case "LOGIN":
                return "Your login OTP is: " + otp;
            case "PASSWORD_RESET":
                return "Your password reset OTP is: " + otp;
            default:
                return "Your OTP is: " + otp;
        }
    }
}
