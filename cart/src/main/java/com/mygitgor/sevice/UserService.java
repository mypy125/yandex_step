package com.mygitgor.sevice;

import com.mygitgor.repository.UserRepository;
import com.mygitgor.util.Log;

public class UserService {
    private final UserRepository userRepository;
    private final CartService cartService;

    public UserService(UserRepository userRepository, CartService cartService) {
        this.userRepository = userRepository;
        this.cartService = cartService;
    }

    public com.mygitgor.model.User createUser(com.mygitgor.dto.UserDto dto){
        if(userRepository.getUser(dto.id())!=null){
            throw new RuntimeException("this user is exist by id"+dto.id());
        }

        com.mygitgor.model.User createdUser = new com.mygitgor.model.User();
        createdUser.setId(dto.id());
        createdUser.setEmail(dto.email());
        userRepository.createUser(createdUser);

        //TODO event to create cart cart_service
        if(cartService.createCart(createdUser.getId())){
            Log.info("cart created by user "+createdUser.getId());
        }

        return createdUser;
    }


}
