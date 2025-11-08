package com.mygitgor.order_service.service;

import com.mygitgor.order_service.domain.Cart;
import com.mygitgor.order_service.domain.CartItem;

import java.util.UUID;

public interface CartService {
    Cart getUserCart(String userId);
    void createUserCart(String userId);
    CartItem addCartItem(UUID user, UUID productId, String size, int quantity);
}
