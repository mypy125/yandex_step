package com.mygitgor.order_service.service;

import com.mygitgor.order_service.domain.Cart;

import java.util.UUID;

public interface CartService {
    Cart getUserCart(UUID userId);
    Cart createUserCart(UUID userId);
}
