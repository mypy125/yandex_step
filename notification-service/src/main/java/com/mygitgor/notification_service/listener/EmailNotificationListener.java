package com.mygitgor.notification_service.listener;

import com.mygitgor.notification_service.dto.EmailNotificationMessage;
import com.mygitgor.notification_service.dto.OtpNotificationMessage;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import com.mygitgor.notification_service.config.RabbitConfig;
import com.mygitgor.notification_service.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailNotificationListener {
    private final EmailService emailService;

    @RabbitListener(queues = RabbitConfig.EMAIL_OTP_QUEUE)
    public void handleOtpEmail(OtpNotificationMessage message) {
        String messageId = generateMessageId();
        try {
            log.info("[{}] Processing OTP email message for: {}, purpose: {}",
                    messageId,  message.getEmail(), message.getPurpose());

            if (message.getEmail() == null || message.getOtp() == null) {
                log.error("[{}] Missing required fields in OTP email. Email: {}, OTP: {}",
                        messageId, message.getEmail(), message.getOtp());
                throw new IllegalArgumentException("Missing required fields: 'email' or 'otp'");
            }
            log.info("[{}] Sending OTP email to: {}, OTP: {}", messageId, message.getEmail(), message.getOtp());

            emailService.sendVerificationOtpEmail(
                    message.getEmail(),
                    message.getOtp()
            );
            log.info("[{}] OTP email processed successfully for: {}", messageId, message.getEmail());

        } catch (IllegalArgumentException e) {
            log.error("[{}] Invalid OTP email message format: {}", messageId, e.getMessage());
            throw new AmqpRejectAndDontRequeueException("Invalid message format: " + e.getMessage());
        } catch (Exception e) {
            log.error("[{}] Failed to process OTP email. Error: {}", messageId, e.getMessage(), e);
            throw new AmqpRejectAndDontRequeueException("Failed to process OTP email: " + e.getMessage());
        }
    }

    @RabbitListener(queues = RabbitConfig.EMAIL_NOTIFICATION_QUEUE)
    public void handleGeneralEmail(EmailNotificationMessage message) {
        String messageId = generateMessageId();
        try {
            log.info("[{}] Processing general email message for: {}, subject: {}",
                    messageId,  message.getEmail(), message.getSubject());

            if (message.getEmail() == null || message.getSubject() == null) {
                log.error("[{}] Missing required fields in general email. Email: {}, Subject: {}",
                        messageId, message.getEmail(), message.getSubject());
                throw new IllegalArgumentException("Missing required fields: 'email' or 'subject'");
            }

            log.info("[{}] Sending general email to: {}, subject: {}, template: {}",
                    messageId, message.getEmail(), message.getSubject(), message.getTemplateName());


            String emailContent = buildEmailContentFromMessage(message);
            emailService.sendGeneralEmail(
                    message.getEmail(),
                    message.getSubject(),
                    emailContent
            );
            log.info("[{}] General email processed successfully for: {}", messageId, message.getEmail());

        } catch (IllegalArgumentException e) {
            log.error("[{}] Invalid general email message format: {}", messageId, e.getMessage());
            throw new AmqpRejectAndDontRequeueException("Invalid message format: " + e.getMessage());
        } catch (Exception e) {
            log.error("[{}] Failed to process general email. Error: {}", messageId, e.getMessage(), e);
            throw new AmqpRejectAndDontRequeueException("Failed to process general email: " + e.getMessage());
        }
    }

    @RabbitListener(queues = RabbitConfig.EMAIL_OTP_DLQ)
    public void handleOtpEmailDlq(OtpNotificationMessage failedMessage, Message amqpMessage, Channel channel) {
        String messageId = generateMessageId();
        try {
            log.warn("[{}] Processing OTP email from DLQ. Original recipient: {}, Purpose: {}",
                    messageId, failedMessage.getEmail(), failedMessage.getPurpose());

            logFailedOtpEmail(failedMessage);

            channel.basicAck(amqpMessage.getMessageProperties().getDeliveryTag(), false);
            log.info("[{}] OTP DLQ message processed successfully", messageId);

        } catch (Exception e) {
            log.error("[{}] Failed to process OTP email from DLQ", messageId, e);
            rejectMessage(channel, amqpMessage, messageId);
        }
    }

    @RabbitListener(queues = RabbitConfig.EMAIL_NOTIFICATION_DLQ)
    public void handleGeneralEmailDlq(EmailNotificationMessage failedMessage, Message amqpMessage, Channel channel) {
        String messageId = generateMessageId();
        try {
            log.warn("[{}] Processing general email from DLQ. Original recipient: {}, Subject: {}",
                    messageId, failedMessage.getEmail(), failedMessage.getSubject());

            logFailedGeneralEmail(failedMessage);

            channel.basicAck(amqpMessage.getMessageProperties().getDeliveryTag(), false);
            log.info("[{}] General DLQ message processed successfully", messageId);

        } catch (Exception e) {
            log.error("[{}] Failed to process general email from DLQ", messageId, e);
            rejectMessage(channel, amqpMessage, messageId);
        }
    }

    private String buildEmailContentFromMessage(EmailNotificationMessage message) {
        StringBuilder content = new StringBuilder();

        content.append("Notification Details:\n\n");

        if (message.getTemplateName() != null) {
            content.append("Template: ").append(message.getTemplateName()).append("\n");
        }

        if (message.getTemplateData() != null && !message.getTemplateData().isEmpty()) {
            content.append("Data:\n");
            message.getTemplateData().forEach((key, value) ->
                    content.append("  - ").append(key).append(": ").append(value).append("\n")
            );
        }

        if (message.getTimestamp() != null) {
            content.append("\nSent at: ").append(message.getTimestamp());
        }

        return content.toString();
    }

    private void logFailedOtpEmail(OtpNotificationMessage message) {
        log.error("FAILED OTP EMAIL - Email: {}, OTP: {}, Purpose: {}, Role: {}, Time: {}",
                message.getEmail(),
                message.getOtp(),
                message.getPurpose(),
                message.getUserRole(),
                LocalDateTime.now());
    }

    private void logFailedGeneralEmail(EmailNotificationMessage message) {
        log.error("FAILED GENERAL EMAIL - Email: {}, Subject: {}, Template: {}, Time: {}",
                message.getEmail(),
                message.getSubject(),
                message.getTemplateName(),
                LocalDateTime.now());
    }

    private void rejectMessage(Channel channel, Message message, String messageId) {
        try {
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
        } catch (Exception nackException) {
            log.error("[{}] Failed to NACK message", messageId, nackException);
        }
    }

    private String generateMessageId() {
        return UUID.randomUUID().toString().substring(0, 8);
    }
}