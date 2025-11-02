package com.mygitgor.auth_service.dto.seller.embeded;

import lombok.Data;

@Data
public class BusinessDetails {
    private String businessName;
    private String businessEmail;
    private String businessMobile;
    private String businessAddress;
    private String logo;
    private String banner;
}
