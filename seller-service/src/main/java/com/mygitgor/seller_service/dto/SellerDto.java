package com.mygitgor.seller_service.dto;

import com.mygitgor.seller_service.domain.AccountStatus;
import com.mygitgor.seller_service.domain.Address;
import com.mygitgor.seller_service.domain.details.BankDetails;
import com.mygitgor.seller_service.domain.details.BusinessDetails;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class SellerDto {
    private UUID id;
    private String sellerName;
    private String email;
    private String mobile;
    private String password;
    private USER_ROLE role;
    private BusinessDetails businessDetails;
    private BankDetails bankDetails;
    private Address pickupAddress;
    private String NDS;
    private boolean emailVerified;
    private AccountStatus accountStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
