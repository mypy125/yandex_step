package com.mygitgor.user_service.dto;

import lombok.Data;

@Data
public class VerificationRequest{
    private String email;
    private String ot;
}