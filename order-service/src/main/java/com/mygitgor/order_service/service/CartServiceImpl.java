package com.mygitgor.order_service.service;

import com.mygitgor.order_service.domain.Cart;
import com.mygitgor.order_service.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;

    @Override
    public Cart getUserCart(UUID userId) {
        return null;
    }

    @Override
    public Cart createUserCart(UUID userId) {
        return null;
    }
}
