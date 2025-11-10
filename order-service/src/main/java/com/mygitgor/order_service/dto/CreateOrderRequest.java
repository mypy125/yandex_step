package com.mygitgor.order_service.dto;

import com.mygitgor.order_service.dto.clientDto.AddressDto;
import lombok.Data;

@Data
public class CreateOrderRequest {
    private String shippingAddress;
}
