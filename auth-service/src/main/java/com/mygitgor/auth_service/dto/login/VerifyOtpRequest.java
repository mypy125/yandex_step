package com.mygitgor.auth_service.dto.login;

import lombok.Data;

@Data
public class VerifyOtpRequest {
    private String otp;
    private String email;
}