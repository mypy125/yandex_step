package com.mygitgor.auth_service.dto.user;

import com.mygitgor.auth_service.dto.USER_ROLE;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private UUID id;
    private String email;
    private String fullName;
    private USER_ROLE role;
    private String otp;
}
