package com.mygitgor.notification_service.listener;

import com.mygitgor.notification_service.config.RabbitConfig;
import com.mygitgor.notification_service.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailNotificationListener {

    private final EmailService emailService;

    @RabbitListener(queues = RabbitConfig.EMAIL_QUEUE)
    public void receiveOtpEmail(Map<String, String> msg) {
        String email = msg.get("to");
        String otp = msg.get("otp");
        emailService.sendVerificationOtpEmail(email, otp);
    }
}