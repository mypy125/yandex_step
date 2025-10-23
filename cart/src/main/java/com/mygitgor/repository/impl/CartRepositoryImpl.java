package com.mygitgor.repository.impl;

import com.mygitgor.repository.CartRepository;

import java.util.HashMap;
import java.util.Map;

public class CartRepositoryImpl implements CartRepository {
    private Map<String, com.mygitgor.model.Cart>map=new HashMap<>();

    @Override
    public com.mygitgor.model.Cart getUserCart(String userId) {
        for (com.mygitgor.model.Cart cart : map.values()){
            if(cart.getUserId().equals(userId))return cart;
        }
        throw new IllegalArgumentException("cart by userid not exist"+userId);
    }

    @Override
    public boolean createCart(com.mygitgor.model.Cart cart) {
        if(!isExist(cart.getId())){
            map.put(cart.getId(), cart);
        }
        throw new RuntimeException("cart byId exist in storage"+cart.getId());
    }

    private boolean isExist(String id) {
        return map.containsKey(id);
    }
}
