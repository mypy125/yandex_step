package com.mygitgor.auth_service.dto.user;

import com.mygitgor.auth_service.dto.USER_ROLE;
import lombok.Data;

@Data
public class UserInfo {
    private String email;
    private USER_ROLE role;
    private String fullName;
    private boolean emailVerified;
}