package com.mygitgor.cart_service.service;

import com.mygitgor.cart_service.domain.Cart;
import com.mygitgor.cart_service.domain.CartItem;
import com.mygitgor.cart_service.dto.CartDto;
import com.mygitgor.cart_service.dto.CartItemDto;

import java.util.UUID;

public interface CartService {
    CartDto getUserCart(String userId);
    void createUserCart(String userId);
    CartItemDto addCartItem(UUID user, UUID productId, String size, int quantity);
}
