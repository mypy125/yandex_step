package com.mygitgor.auth_service.repository;

import com.mygitgor.auth_service.domain.BlacklistedToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface BlacklistedTokenRepository extends JpaRepository<BlacklistedToken, UUID> {
    boolean existsByToken(String token);
    Optional<BlacklistedToken> findByToken(String token);
    void deleteByExpiresAtBefore(LocalDateTime dateTime);
}
