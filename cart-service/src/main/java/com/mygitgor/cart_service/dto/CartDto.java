package com.mygitgor.cart_service.dto;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class CartDto {
    private UUID id;
    private String userId;
    private List<CartItemDto> cartItems;
    private double totalSellingPrice;
    private Integer totalItem;
    private Integer totalMrpPrice;
    private Integer discount;
    private String couponCode;
}
