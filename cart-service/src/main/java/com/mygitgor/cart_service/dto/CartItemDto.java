package com.mygitgor.cart_service.dto;

import com.mygitgor.cart_service.domain.Cart;
import lombok.Data;

import java.util.UUID;

@Data
public class CartItemDto {
    private UUID id;
    private Cart cart;
    private UUID productId;
    private String size;
    private Integer quantity;
    private Integer mrpPrice;
    private Integer sellingPrice;
    private UUID userId;
}
