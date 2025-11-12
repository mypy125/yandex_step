package com.mygitgor.cart_service.service.impl;

import com.mygitgor.cart_service.domain.Cart;
import com.mygitgor.cart_service.domain.CartItem;
import com.mygitgor.cart_service.dto.CartDto;
import com.mygitgor.cart_service.dto.CartItemDto;
import com.mygitgor.cart_service.mapping.CartMapper;
import com.mygitgor.cart_service.repository.CartRepository;
import com.mygitgor.cart_service.service.CartService;
import com.sun.jdi.request.DuplicateRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;
    private final CartMapper cartMapper;

    @Override
    public CartDto getUserCart(String userId) {
        if(!cartRepository.existsByUserId(userId)){
            throw new RuntimeException(String.format(
                    "cart by userid '%s' not found",userId
            ));
        }
        Cart userCart =  cartRepository.findByUserId(userId);
        return cartMapper.toCartDto(userCart);
    }

    @Override
    public void createUserCart(String userId) {
        if (cartRepository.existsByUserId(userId)) {
            throw new DuplicateRequestException("Cart already exists for user: " + userId);
        }
        Cart cart = Cart.create(userId);
        cartRepository.save(cart);
    }

    @Override
    public CartItemDto addCartItem(UUID user, UUID productId, String size, int quantity) {
        return null;
    }
}

