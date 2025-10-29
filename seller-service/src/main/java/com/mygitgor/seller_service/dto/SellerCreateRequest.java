package com.mygitgor.seller_service.dto;

import lombok.Data;

@Data
public class SellerCreateRequest {
    private String email;
    private String fullName;
    private String otp;
}