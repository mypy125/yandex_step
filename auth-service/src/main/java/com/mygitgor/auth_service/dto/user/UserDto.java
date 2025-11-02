package com.mygitgor.auth_service.dto.user;

import lombok.Data;

@Data
public class UserDto {
    private String email;
    private String fullName;
    private String otp;
    private String mobile;
}
