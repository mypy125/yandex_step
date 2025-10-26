package com.mygitgor.user_management_service.service;

import com.mygitgor.user_management_service.domain.VerificationCode;
import com.mygitgor.user_management_service.repository.VerificationCodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class VerificationService {
    private final VerificationCodeRepository repository;

    public void saveVerificationCode(String email, String otp) {
        VerificationCode existing = repository.findByEmail(email);
        if (existing != null) {
            repository.delete(existing);
        }

        VerificationCode code = new VerificationCode();
        code.setEmail(email);
        code.setOtp(otp);
        code.setCreatedAt(LocalDateTime.now());
        repository.save(code);
    }

    public boolean verifyOtp(String email, String otp) {
        VerificationCode code = repository.findByEmail(email);
        if (code == null) return false;
        return !code.isExpired() && code.getOtp().equals(otp);
    }
}
