package com.mygitgor.user_management_service.dto;

import com.mygitgor.user_management_service.domain.USER_ROLE;

import java.util.List;
import java.util.UUID;

public record UserDto (
        UUID id,
        String fullName,
        String email,
        List<USER_ROLE> roles
){
    public List<USER_ROLE> getRoles() {
        return roles != null ? roles : List.of(USER_ROLE.ROLE_CUSTOMER);
    }
}
