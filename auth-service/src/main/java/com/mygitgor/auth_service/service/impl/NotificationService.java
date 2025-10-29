package com.mygitgor.auth_service.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {
    private final AmqpTemplate amqpTemplate;

    public Mono<Void> sendVerificationOtpEmail(String email, String otp, String subject, String text) {
        return Mono.fromRunnable(() -> {
            try {
                Map<String, String> message = Map.of(
                        "to", email,
                        "otp", otp,
                        "subject", subject,
                        "text", text,
                        "type", "VERIFICATION_OTP"
                );

                amqpTemplate.convertAndSend("notification-exchange", "email.otp", message);
                log.debug("OTP email sent to notification service for: {}", email);
            } catch (Exception e) {
                log.error("Failed to send OTP email to notification service for: {}", email, e);
                throw new RuntimeException("Failed to send email");
            }
        });
    }

    public Mono<Void> sendEmailNotification(String email, String subject, String text, String templateType) {
        return Mono.fromRunnable(() -> {
            try {
                Map<String, String> message = Map.of(
                        "to", email,
                        "subject", subject,
                        "text", text,
                        "templateType", templateType,
                        "type", "GENERAL_EMAIL"
                );

                amqpTemplate.convertAndSend("notification-exchange", "email.notification", message);
                log.debug("General email sent to notification service for: {}", email);
            } catch (Exception e) {
                log.error("Failed to send email to notification service for: {}", email, e);
                throw new RuntimeException("Failed to send email");
            }
        });
    }
}
