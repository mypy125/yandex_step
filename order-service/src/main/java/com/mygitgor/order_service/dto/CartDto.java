package com.mygitgor.order_service.dto;

import java.util.Set;
import java.util.UUID;

public class CartDto {
    private UUID id;
    private String userId;
    private Set<CartItemDto> cartItems;
    private double totalSellingPrice;
    private int totalItem;
    private int totalMrpPrice;
    private int discount;
    private String couponCode;

}
