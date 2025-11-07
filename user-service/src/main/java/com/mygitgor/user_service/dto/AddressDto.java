package com.mygitgor.user_service.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class AddressDto {
    private UUID id;
    private String name;
    private String locality;
    private String address;
    private String city;
    private String state;
    private String pinCode;
    private String mobile;
    private UUID userId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
