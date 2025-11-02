package com.mygitgor.auth_service.dto.seller;

import com.mygitgor.auth_service.dto.USER_ROLE;
import com.mygitgor.auth_service.dto.seller.embeded.AccountStatus;
import com.mygitgor.auth_service.dto.seller.embeded.Address;
import com.mygitgor.auth_service.dto.seller.embeded.BankDetails;
import com.mygitgor.auth_service.dto.seller.embeded.BusinessDetails;
import lombok.Data;

@Data
public class SellerDto {
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
}