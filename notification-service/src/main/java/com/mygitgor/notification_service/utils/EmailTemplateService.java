package com.mygitgor.notification_service.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class EmailTemplateService {

    public String getOtpTemplate(String otpCode) {
        try {
            return buildOtpTemplate(otpCode);
        } catch (Exception e) {
            log.error("Error creating OTP template: {}", e.getMessage());
            return createFallbackTemplate(otpCode);
        }
    }

    public String getSellerVerificationTemplate(String otp, String verificationLink) {
        try {
            return buildSellerVerificationTemplate(otp, verificationLink);
        } catch (Exception e) {
            log.error("Error creating seller verification template: {}", e.getMessage());
            return createFallbackSellerTemplate(otp, verificationLink);
        }
    }

    public String extractVerificationLink(String text) {
        if (text.contains("https://")) {
            int start = text.indexOf("https://");
            int end = text.indexOf(" ", start);
            if (end == -1) end = text.length();
            return text.substring(start, end).trim();
        }
        return text;
    }

    private String buildOtpTemplate(String otpCode) {
        return """
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Verification Code</title>
                <style>
                    * { margin: 0; padding: 0; box-sizing: border-box; }
                    body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); margin: 0; padding: 20px; min-height: 100vh; }
                    .email-container { max-width: 600px; margin: 0 auto; background: white; border-radius: 15px; overflow: hidden; box-shadow: 0 20px 40px rgba(0,0,0,0.1); }
                    .email-header { background: linear-gradient(135deg, #4F46E5 0%, #7C3AED 100%); padding: 40px 30px; text-align: center; color: white; }
                    .email-header h1 { font-size: 28px; font-weight: 300; margin-bottom: 10px; }
                    .email-body { padding: 40px 30px; }
                    .otp-code { font-size: 48px; font-weight: bold; color: #4F46E5; letter-spacing: 8px; margin: 20px 0; font-family: 'Courier New', monospace; text-align: center; }
                    .otp-expiry { color: #6B7280; font-size: 14px; margin-top: 10px; text-align: center; }
                    .info-section { background: #F8FAFC; padding: 20px; border-radius: 10px; margin: 30px 0; border-left: 4px solid #4F46E5; }
                    .email-footer { background: #1F2937; color: #9CA3AF; padding: 30px; text-align: center; font-size: 12px; }
                    @media (max-width: 600px) { .email-body { padding: 30px 20px; } .otp-code { font-size: 36px; letter-spacing: 6px; } }
                </style>
            </head>
            <body>
                <div class="email-container">
                    <div class="email-header">
                        <h1>Verify Your Email</h1>
                        <p>Enter the following code to complete your verification</p>
                    </div>
                    <div class="email-body">
                        <div class="otp-section">
                            <p style="color: #6B7280; margin-bottom: 15px; text-align: center;">Your verification code is:</p>
                            <div class="otp-code">%s</div>
                            <p class="otp-expiry">This code will expire in 10 minutes</p>
                        </div>
                        <div class="info-section">
                            <p><strong>Important:</strong> For your security, please do not share this code with anyone. Our team will never ask for your verification code.</p>
                        </div>
                        <p style="color: #6B7280; text-align: center; font-size: 14px;">
                            If you didn't request this code, please ignore this email or contact support if you have concerns.
                        </p>
                    </div>
                    <div class="email-footer">
                        <p>&copy; 2024 Ecommerce Multivendor. All rights reserved.</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(otpCode);
    }

    private String buildSellerVerificationTemplate(String otp, String verificationLink) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body { font-family: Arial, sans-serif; margin: 0; padding: 20px; background-color: #f4f4f4; }
                    .container { max-width: 600px; margin: 0 auto; background: white; border-radius: 10px; padding: 30px; }
                    .header { background: linear-gradient(135deg, #4F46E5 0%, #7C3AED 100%); color: white; padding: 20px; border-radius: 10px 10px 0 0; text-align: center; }
                    .otp-code { font-size: 32px; font-weight: bold; color: #4F46E5; text-align: center; margin: 20px 0; }
                    .verification-link { display: block; text-align: center; margin: 20px 0; }
                    .button { background: #4F46E5; color: white; padding: 12px 30px; text-decoration: none; border-radius: 5px; font-weight: bold; display: inline-block; }
                    .footer { margin-top: 30px; padding-top: 20px; border-top: 1px solid #eee; color: #666; text-align: center; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>Welcome to Ecommerce Multivendor!</h1>
                        <p>Your Seller Account Verification</p>
                    </div>
                    <div style="padding: 20px;">
                        <p>Thank you for registering as a seller. Please verify your account using one of the methods below:</p>
                        
                        <div class="otp-code">%s</div>
                        <p style="text-align: center; color: #666;">Enter this code OR click the button below:</p>
                        
                        <div class="verification-link">
                            <a href="%s" class="button">Verify Your Seller Account</a>
                        </div>
                        
                        <p style="color: #666; font-size: 14px; margin-top: 20px;">
                            If the button doesn't work, copy and paste this link in your browser:<br>
                            <span style="color: #4F46E5; word-break: break-all;">%s</span>
                        </p>
                    </div>
                    <div class="footer">
                        <p>This verification link will expire in 24 hours.</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(otp, verificationLink, verificationLink);
    }

    private String createFallbackTemplate(String otpCode) {
        return """
            <div style="font-family: Arial, sans-serif; padding: 20px; text-align: center;">
                <h2 style="color: #333;">Verify Your Email</h2>
                <p>Your verification code is:</p>
                <div style="font-size: 32px; font-weight: bold; color: #4F46E5; margin: 20px 0;">
                    %s
                </div>
                <p style="color: #666;">This code will expire in 10 minutes.</p>
            </div>
            """.formatted(otpCode);
    }

    private String createFallbackSellerTemplate(String otp, String verificationLink) {
        return """
            <div style="font-family: Arial, sans-serif; padding: 20px;">
                <h2>Welcome Seller!</h2>
                <p>Your verification code: <strong>%s</strong></p>
                <p>Or verify using this link: <a href="%s">%s</a></p>
            </div>
            """.formatted(otp, verificationLink, verificationLink);
    }
}
