package com.mygitgor.auth_service.dto.seller;

import lombok.Data;

@Data
public class SellerCreateRequest {
    private String email;
    private String fullName;
    private String otp;
}