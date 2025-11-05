package com.mygitgor.notification_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OtpNotificationMessage {
    private String email;
    private String otp;
    private String purpose;
    private String userRole;
    private LocalDateTime timestamp;
}