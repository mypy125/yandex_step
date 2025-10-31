package com.mygitgor.seller_service.domain.details;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class BankDetails {
    private String accountNumber;
    private String accountHolderName;
    private String bankName;
    private String bankCode;
}
