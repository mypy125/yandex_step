package com.mygitgor.order_service.service;

import com.mygitgor.order_service.domain.Cart;

public interface CartService {
    Cart getUserCart(String userId);
    void createUserCart(String userId);
}
