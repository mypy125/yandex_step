package com.mygitgor.notification_service.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailNotificationListener {
    private final EmailService emailService;
    private final ObjectMapper objectMapper;

    @RabbitListener(queues = RabbitConfig.EMAIL_OTP_QUEUE)
    public void handleOtpEmail(Message message) {
        String messageId = generateMessageId();
        try {
            Map<String, String> emailMessage = convertMessageBody(message);

            log.info("[{}] Processing OTP email message: {}", messageId, emailMessage.keySet());

            String to = emailMessage.get("to");
            String otp = emailMessage.get("otp");
            String subject = emailMessage.get("subject");
            String text = emailMessage.get("text");
            String type = emailMessage.get("type");

            if (to == null || otp == null) {
                log.error("❌ [{}] Missing required fields in OTP email. To: {}, OTP: {}",
                        messageId, to, otp);
                throw new IllegalArgumentException("Missing required fields: 'to' or 'otp'");
            }

            if (subject == null) {
                subject = "Your Verification Code - Ecommerce Multivendor";
            }

            if (text == null) {
                text = "";
            }

            log.info("[{}] Sending OTP email to: {}, subject: {}", messageId, to, subject);
            emailService.sendVerificationOtpEmail(to, otp, subject, text);
            log.info("[{}] OTP email processed successfully for: {}", messageId, to);

        } catch (IllegalArgumentException e) {
            log.error("[{}] Invalid OTP email message format: {}", messageId, e.getMessage());
            throw new AmqpRejectAndDontRequeueException("Invalid message format: " + e.getMessage());
        } catch (Exception e) {
            log.error("[{}] Failed to process OTP email. Error: {}", messageId, e.getMessage(), e);
            throw new AmqpRejectAndDontRequeueException("Failed to process OTP email: " + e.getMessage());
        }
    }

    @RabbitListener(queues = RabbitConfig.EMAIL_NOTIFICATION_QUEUE)
    public void handleGeneralEmail(Message message) {
        String messageId = generateMessageId();
        try {
            Map<String, String> emailMessage = convertMessageBody(message);

            log.info("[{}] Processing general email message: {}", messageId, emailMessage.keySet());

            String to = emailMessage.get("to");
            String subject = emailMessage.get("subject");
            String text = emailMessage.get("text");
            String templateType = emailMessage.get("templateType");

            if (to == null || subject == null || text == null) {
                log.error("[{}] Missing required fields in general email. To: {}, Subject: {}, Text: {}",
                        messageId, to, subject, text);
                throw new IllegalArgumentException("Missing required fields: 'to', 'subject' or 'text'");
            }

            log.info("[{}] Sending general email to: {}, subject: {}, template: {}",
                    messageId, to, subject, templateType);

            emailService.sendVerificationOtpEmail(to, "GENERAL_OTP", subject, text);
            log.info("[{}] General email processed successfully for: {}", messageId, to);

        } catch (IllegalArgumentException e) {
            log.error("[{}] Invalid general email message format: {}", messageId, e.getMessage());
            throw new AmqpRejectAndDontRequeueException("Invalid message format: " + e.getMessage());
        } catch (Exception e) {
            log.error("[{}] Failed to process general email. Error: {}", messageId, e.getMessage(), e);
            throw new AmqpRejectAndDontRequeueException("Failed to process general email: " + e.getMessage());
        }
    }

    @RabbitListener(queues = RabbitConfig.EMAIL_OTP_DLQ)
    public void handleOtpEmailDlq(Message failedMessage, Channel channel) {
        String messageId = generateMessageId();
        try {
            Map<String, String> originalMessage = convertMessageBody(failedMessage);

            log.warn("[{}] Processing OTP email from DLQ. Original recipient: {}, Headers: {}",
                    messageId,
                    originalMessage.get("to"),
                    failedMessage.getMessageProperties().getHeaders());

            logFailedOtpEmail(originalMessage);

            channel.basicAck(failedMessage.getMessageProperties().getDeliveryTag(), false);
            log.info("[{}] OTP DLQ message processed successfully", messageId);

        } catch (Exception e) {
            log.error("[{}] Failed to process OTP email from DLQ", messageId, e);
            rejectMessage(channel, failedMessage, messageId);
        }
    }

    @RabbitListener(queues = RabbitConfig.EMAIL_NOTIFICATION_DLQ)
    public void handleGeneralEmailDlq(Message failedMessage, Channel channel) {
        String messageId = generateMessageId();
        try {
            Map<String, String> originalMessage = convertMessageBody(failedMessage);

            log.warn("[{}] Processing general email from DLQ. Original recipient: {}, Headers: {}",
                    messageId,
                    originalMessage.get("to"),
                    failedMessage.getMessageProperties().getHeaders());

            logFailedGeneralEmail(originalMessage);

            channel.basicAck(failedMessage.getMessageProperties().getDeliveryTag(), false);
            log.info("[{}] General DLQ message processed successfully", messageId);

        } catch (Exception e) {
            log.error("[{}] Failed to process general email from DLQ", messageId, e);
            rejectMessage(channel, failedMessage, messageId);
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, String> convertMessageBody(Message message) {
        try {
            String messageBody = new String(message.getBody(), "UTF-8");
            return objectMapper.readValue(messageBody, Map.class);
        } catch (Exception e) {
            log.error("Failed to deserialize message body. Body: {}", new String(message.getBody()), e);
            throw new AmqpRejectAndDontRequeueException("Invalid message format: " + e.getMessage());
        }
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

    private void logFailedOtpEmail(Map<String, String> message) {
        log.error("FAILED OTP EMAIL - To: {}, OTP: {}, Time: {}",
                message.get("to"), message.get("otp"), LocalDateTime.now());
    }

    private void logFailedGeneralEmail(Map<String, String> message) {
        log.error("FAILED GENERAL EMAIL - To: {}, Subject: {}, Time: {}",
                message.get("to"), message.get("subject"), LocalDateTime.now());
    }
}