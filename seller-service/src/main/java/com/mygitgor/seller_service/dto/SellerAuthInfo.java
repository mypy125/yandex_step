package com.mygitgor.seller_service.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class SellerAuthInfo {
    private UUID id;
    private String email;
    private String fullName;
    private USER_ROLE role;
    private boolean emailVerified;
}
