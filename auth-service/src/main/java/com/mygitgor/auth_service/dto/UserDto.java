package com.mygitgor.auth_service.dto;

import java.util.List;
import java.util.UUID;

public record UserDto (UUID id,String fullName,String email,List<String> roles){

    public List<String> getRoles() {
        return roles != null ? roles : List.of("ROLE_CUSTOMER");
    }
}
