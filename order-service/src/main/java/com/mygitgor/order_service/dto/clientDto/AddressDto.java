package com.mygitgor.order_service.dto.clientDto;

import lombok.Data;

@Data
public class AddressDto {
    private String name;
    private String locality;
    private String address;
    private String city;
    private String state;
    private String pinCode;
    private String mobile;
}
