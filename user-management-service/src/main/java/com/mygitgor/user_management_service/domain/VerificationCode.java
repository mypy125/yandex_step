package com.mygitgor.user_management_service.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "verification_codes")
@EqualsAndHashCode(callSuper = false)
public class VerificationCode extends BaseEntity{
    @Column(nullable = false, length = 6)
    private String otp;

    @Column(nullable = false)
    private String email;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private UUID sellerId;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public boolean isExpired() {
        return createdAt.isBefore(LocalDateTime.now().minusMinutes(5));
    }
}
