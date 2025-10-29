package com.mygitgor.seller_service.dto;

import lombok.Data;

@Data
public class SellerAuthInfo {
    private String id;
    private String email;
    private String fullName;
    private USER_ROLE role;
    private boolean emailVerified;
}
