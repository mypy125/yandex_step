package com.mygitgor.auth_service.dto.login;

import com.mygitgor.auth_service.dto.USER_ROLE;
import lombok.Data;

@Data
public class OtpRequest {
    private String email;
    private USER_ROLE role;
    private String purpose;
}
