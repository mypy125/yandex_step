package com.mygitgor.auth_service.dto;

public record SignupRequest (
        String email,
        String fullName,
        String otp
){
}
