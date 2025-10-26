package com.mygitgor.auth_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class OtpSenderService {
    private final AmqpTemplate amqpTemplate;

    public void sendOtpToNotificationService(String email, String otp) {
        Map<String, String> msg = Map.of("to", email, "otp", otp);
        amqpTemplate.convertAndSend("notification-exchange", "email.notification", msg);
    }
}
