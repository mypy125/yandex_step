package com.mygitgor.user_management_service.service;

public interface NotificationService {
    void sendOtpToNotificationService(String email, String otp);
}
