package com.mygitgor.auth_service.service;

import com.mygitgor.auth_service.domain.VerificationCode;
import com.mygitgor.auth_service.dto.OtpPurpose;
import com.mygitgor.auth_service.dto.OtpRequest;
import com.mygitgor.auth_service.dto.USER_ROLE;
import com.mygitgor.auth_service.dto.VerifyOtpRequest;
import com.mygitgor.auth_service.dto.response.ApiResponse;
import reactor.core.publisher.Mono;

import java.util.Optional;

public interface VerificationService {
    Mono<VerificationCode> sendOtp(String email, USER_ROLE role, String purpose);
    Mono<Boolean> validateOtp(String email, String otp, String purpose);
    Mono<Boolean> verifyOtp(String otp, VerifyOtpRequest request);
}
