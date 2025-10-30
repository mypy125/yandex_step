package com.mygitgor.notification_service.service;

import com.mygitgor.notification_service.utils.EmailTemplateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import sendinblue.ApiClient;
import sendinblue.Configuration;
import sibApi.TransactionalEmailsApi;
import sibModel.CreateSmtpEmail;
import sibModel.SendSmtpEmail;
import sibModel.SendSmtpEmailSender;
import sibModel.SendSmtpEmailTo;

import java.util.List;

@Slf4j
@Service
public class EmailService {

    @Value("${brevo.api-key}")
    private String brevoApiKey;

    @Value("${brevo.from-email}")
    private String fromEmail;

    @Value("${brevo.from-name:Ecommerce Multivendor}")
    private String fromName;

    private final EmailTemplateService templateService;

    public EmailService(EmailTemplateService templateService) {
        this.templateService = templateService;
    }

    public void sendVerificationOtpEmail(String userEmail, String otp) {
        sendVerificationOtpEmail(userEmail, otp,
                "Your Verification Code - Ecommerce Multivendor",
                "");
    }

    public void sendVerificationOtpEmail(String userEmail, String otp, String subject, String text) {
        try {
            log.info("sendVerificationOtpEmail to: {}", userEmail);
            log.info("OTP: {}, Subject: {}", otp, subject);

            ApiClient defaultClient = Configuration.getDefaultApiClient();
            defaultClient.setApiKey(brevoApiKey);

            TransactionalEmailsApi api = new TransactionalEmailsApi();

            String htmlContent = buildCustomEmailContent(otp, subject, text);

            SendSmtpEmail sendSmtpEmail = new SendSmtpEmail();
            sendSmtpEmail.setTo(List.of(new SendSmtpEmailTo().email(userEmail)));
            sendSmtpEmail.setSubject(subject);
            sendSmtpEmail.setHtmlContent(htmlContent);
            sendSmtpEmail.setSender(new SendSmtpEmailSender()
                    .email(fromEmail)
                    .name(fromName));

            log.info("Sending email via Brevo...");
            CreateSmtpEmail response = api.sendTransacEmail(sendSmtpEmail);

            log.info("sendVerificationOtpEmail sent successfully to: {}. Message ID: {}", userEmail, response.getMessageId());

        } catch (Exception e) {
            log.error("sendVerificationOtpEmail error for {}: {}", userEmail, e.getMessage());
            log.info("OTP for {}: {} (email failed)", userEmail, otp);
        }
    }

    private String buildCustomEmailContent(String otp, String subject, String text) {
        if (text.contains("verify-seller") || text.contains("verificationLink")) {
            String verificationLink = templateService.extractVerificationLink(text);
            return templateService.getSellerVerificationTemplate(otp, verificationLink);
        }
        return templateService.getOtpTemplate(otp);
    }


    public void sendGeneralEmail(String userEmail, String subject, String text) {
        try {
            log.info("sendGeneralEmail to: {}, subject: {}", userEmail, subject);

            ApiClient defaultClient = Configuration.getDefaultApiClient();
            defaultClient.setApiKey(brevoApiKey);

            TransactionalEmailsApi api = new TransactionalEmailsApi();

            String htmlContent = templateService.buildGeneralEmailContent(subject, text);

            SendSmtpEmail sendSmtpEmail = new SendSmtpEmail();
            sendSmtpEmail.setTo(List.of(new SendSmtpEmailTo().email(userEmail)));
            sendSmtpEmail.setSubject(subject);
            sendSmtpEmail.setHtmlContent(htmlContent);
            sendSmtpEmail.setSender(new SendSmtpEmailSender()
                    .email(fromEmail)
                    .name(fromName));

            CreateSmtpEmail response = api.sendTransacEmail(sendSmtpEmail);

            log.info("sendGeneralEmail sent successfully to: {}. Message ID: {}", userEmail, response.getMessageId());

        } catch (Exception e) {
            log.error("Brevo error for sendGeneralEmail to {}: {}", userEmail, e.getMessage());
            throw new RuntimeException("sendGeneralEmail sending failed: " + e.getMessage());
        }
    }






}