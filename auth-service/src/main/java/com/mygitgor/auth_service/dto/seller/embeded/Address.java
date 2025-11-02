package com.mygitgor.auth_service.dto.seller.embeded;

import lombok.Data;

@Data
public class Address {
    private String name;
    private String locality;
    private String address;
    private String city;
    private String state;
    private String pinCode;
    private String mobile;
}
