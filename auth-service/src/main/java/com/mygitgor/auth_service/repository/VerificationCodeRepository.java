package com.mygitgor.auth_service.repository;

import com.mygitgor.auth_service.domain.VerificationCode;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface VerificationCodeRepository extends JpaRepository<VerificationCode, UUID> {
    VerificationCode findByEmailAndOtpAndPurpose(String email, String otp, String purpose);
    List<VerificationCode> findByEmailAndPurpose(String email, String purpose);
    List<VerificationCode> findByEmail(String email);
}
