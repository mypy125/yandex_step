package com.mygitgor.seller_service.dto;

import com.mygitgor.seller_service.domain.AccountStatus;
import com.mygitgor.seller_service.domain.Address;
import com.mygitgor.seller_service.domain.details.BankDetails;
import com.mygitgor.seller_service.domain.details.BusinessDetails;
import lombok.Data;

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
    private Boolean emailVerified;
    private AccountStatus accountStatus;

    public Boolean getEmailVerified() {
        return emailVerified != null ? emailVerified : false;
    }

    public AccountStatus getAccountStatus() {
        return accountStatus != null ? accountStatus : AccountStatus.PENDING_VERIFICATION;
    }

    public USER_ROLE getRole() {
        return role != null ? role : USER_ROLE.ROLE_SELLER;
    }
}
