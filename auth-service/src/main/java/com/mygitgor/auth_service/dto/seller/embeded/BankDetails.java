package com.mygitgor.auth_service.dto.seller.embeded;

import lombok.Data;

@Data
public class BankDetails {
    private String accountNumber;
    private String accountHolderName;
    private String bankName;
    private String bankCode;
}
