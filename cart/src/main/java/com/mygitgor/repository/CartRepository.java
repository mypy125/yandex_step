package com.mygitgor.repository;

import com.mygitgor.model.Cart;

public interface CartRepository {
    Cart getUserCart(String userId);
    boolean createCart(Cart cart);

}
