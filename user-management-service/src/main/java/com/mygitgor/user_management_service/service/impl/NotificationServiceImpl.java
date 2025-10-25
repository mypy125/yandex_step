package com.mygitgor.user_management_service.service.impl;

import com.mygitgor.user_management_service.config.RabbitConfig;
import com.mygitgor.user_management_service.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final AmqpTemplate amqpTemplate;

    @Override
    public void sendOtpToNotificationService(String email, String otp) {
        Map<String, String> msg = Map.of("to", email, "otp", otp);
        amqpTemplate.convertAndSend(
                RabbitConfig.EXCHANGE,
                RabbitConfig.ROUTING_KEY,
                msg
        );
    }
}
