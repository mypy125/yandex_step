package com.mygitgor.auth_service.dto.response;

import com.mygitgor.auth_service.dto.USER_ROLE;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {
    private String jwt;
    private String message;
    private USER_ROLE role;
    private String email;
    private LocalDateTime timestamp;

    public AuthResponse(String jwt, String message, USER_ROLE role) {
        this.jwt = jwt;
        this.message = message;
        this.role = role;
        this.timestamp = LocalDateTime.now();
    }
}
