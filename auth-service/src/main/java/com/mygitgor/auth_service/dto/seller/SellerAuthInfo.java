package com.mygitgor.auth_service.dto.seller;

import com.mygitgor.auth_service.dto.USER_ROLE;
import lombok.Data;

@Data
public class SellerAuthInfo {
    private String id;
    private String email;
    private String fullName;
    private USER_ROLE role;
    private boolean emailVerified;
}
