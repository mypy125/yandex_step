package com.mygitgor.sevice;

import com.mygitgor.model.CartItem;
import com.mygitgor.repository.CartRepository;

import java.math.BigDecimal;

public class CartService {
    private final CartRepository cartRepository;
    private final CartItemService cartItemService;

    public CartService(CartRepository cartRepository, CartItemService cartItemService) {
        this.cartRepository = cartRepository;
        this.cartItemService = cartItemService;
    }

    public boolean createCart(String userId){
        com.mygitgor.model.Cart createdCart = new com.mygitgor.model.Cart();
        createdCart.setUserId(userId);
        createdCart.setAmount(BigDecimal.ZERO);
        return cartRepository.createCart(createdCart);
    }

    public com.mygitgor.model.Cart getUserCart(String userId){
        return cartRepository.getUserCart(userId);
    }

    public com.mygitgor.model.Cart updateCartAmount(String userId){
        com.mygitgor.model.Cart userCart = getUserCart(userId);
        cartItemService.updatePercentDiscountUser(userId,userCart.getItems());

        BigDecimal total = BigDecimal.ZERO;
        for(CartItem item : userCart.getItems()){
            if (item.getPrice() != null) {
                total = total.add(item.getPrice());
            }
        }
        userCart.setAmount(total);
        return userCart;
    }

}
