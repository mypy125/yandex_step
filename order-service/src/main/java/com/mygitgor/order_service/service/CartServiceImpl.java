package com.mygitgor.order_service.service;

import com.mygitgor.order_service.domain.Cart;
import com.mygitgor.order_service.repository.CartRepository;
import com.sun.jdi.request.DuplicateRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;

    @Override
    public Cart getUserCart(String userId) {
        return null;
    }

    @Override
    public void createUserCart(String userId) {
        if (cartRepository.existsByUserId(userId)) {
            throw new DuplicateRequestException("Cart already exists for user: " + userId);
        }
        Cart cart = Cart.create(userId);
        cartRepository.save(cart);
    }
}
