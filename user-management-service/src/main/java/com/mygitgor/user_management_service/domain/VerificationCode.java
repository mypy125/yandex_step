package com.mygitgor.user_management_service.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "verification_codes")
@ToString(callSuper = true, exclude = "user")
@EqualsAndHashCode(callSuper = true, exclude = "user")
public class VerificationCode extends BaseEntity{
    @Column(nullable = false, length = 6)
    private String otp;

    @Column(nullable = false)
    private String email;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JoinColumn(name = "user_id")
    private User user;

    private UUID sellerId;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public VerificationCode(String otp, String email, User user) {
        this.otp = otp;
        this.email = email;
        this.user = user;
    }

    public VerificationCode(String otp, String email, UUID sellerId) {
        this.otp = otp;
        this.email = email;
        this.sellerId = sellerId;
    }

    public boolean isExpired() {
        return createdAt.isBefore(LocalDateTime.now().minusMinutes(5));
    }

    public static VerificationCode createForUser(String otp, String email, User user) {
        return new VerificationCode(otp, email, user);
    }

    public static VerificationCode createForSeller(String otp, String email, UUID sellerId) {
        return new VerificationCode(otp, email, sellerId);
    }
}
