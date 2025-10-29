package com.mygitgor.auth_service.dto.user;

import lombok.Data;

@Data
public class UserCreateRequest {
    private String email;
    private String fullName;
    private String otp;
    private String mobile;
}
