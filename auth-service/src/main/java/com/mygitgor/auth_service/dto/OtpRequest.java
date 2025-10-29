package com.mygitgor.auth_service.dto;

import lombok.Data;

@Data
public class OtpRequest {
    private String email;
    private USER_ROLE role;
    private String purpose;
}
