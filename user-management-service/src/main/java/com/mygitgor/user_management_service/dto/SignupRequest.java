package com.mygitgor.user_management_service.dto;

public record SignupRequest (
        String email,
        String fullName,
        String otp
){
}