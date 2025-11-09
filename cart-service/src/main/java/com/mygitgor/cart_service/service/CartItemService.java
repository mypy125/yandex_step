package com.mygitgor.cart_service.service;

import com.mygitgor.cart_service.domain.CartItem;

import java.util.Optional;
import java.util.UUID;

public interface CartItemService {
    CartItem updateCartItem(UUID userId, UUID id, CartItem cartItem);
    void removeCartItem(UUID userId, UUID cartItemId) throws Exception;
    Optional<CartItem> findCartItemById(UUID id) throws Exception;
}
