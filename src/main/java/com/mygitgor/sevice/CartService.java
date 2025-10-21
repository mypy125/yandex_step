package com.mygitgor.sevice;

import com.mygitgor.model.Cart;
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
        Cart createdCart = new Cart();
        createdCart.setUserId(userId);
        createdCart.setAmount(BigDecimal.ZERO);
        return cartRepository.createCart(createdCart);
    }

    public Cart getUserCart(String userId){
        return cartRepository.getUserCart(userId);
    }

    public Cart updateCartAmount(String userId){
        Cart userCart = getUserCart(userId);
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
