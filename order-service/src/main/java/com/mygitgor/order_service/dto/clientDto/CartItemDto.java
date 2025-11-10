package com.mygitgor.order_service.dto.clientDto;

import lombok.Data;

import java.util.UUID;

@Data
public class CartItemDto {
    private UUID id;
    private UUID productId;
    private String size;
    private Integer quantity;
    private Integer mrpPrice;
    private Integer sellingPrice;
}
