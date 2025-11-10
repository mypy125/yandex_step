package com.mygitgor.order_service.client;

import com.mygitgor.order_service.dto.clientDto.CartDto;

public class CartClient {
    public CartDto getCartByUserId(String userId){
        return new CartDto();
    }

    public void clearCart(String cartId){

    }

}
