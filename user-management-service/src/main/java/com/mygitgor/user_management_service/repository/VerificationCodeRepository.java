package com.mygitgor.user_management_service.repository;

import com.mygitgor.user_management_service.domain.VerificationCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface VerificationCodeRepository extends JpaRepository<VerificationCode,UUID> {
    VerificationCode findByEmail(String email);
}
