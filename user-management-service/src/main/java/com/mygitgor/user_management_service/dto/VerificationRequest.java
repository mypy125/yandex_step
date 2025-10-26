package com.mygitgor.user_management_service.dto;

public record VerificationRequest(String email, String otp) {
}