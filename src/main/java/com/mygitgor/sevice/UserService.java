package com.mygitgor.sevice;

import com.mygitgor.dto.UserDto;
import com.mygitgor.model.User;
import com.mygitgor.repository.UserRepository;
import com.mygitgor.util.Log;

public class UserService {
    private final UserRepository userRepository;
    private final CartService cartService;

    public UserService(UserRepository userRepository, CartService cartService) {
        this.userRepository = userRepository;
        this.cartService = cartService;
    }

    public User createUser(UserDto dto){
        if(userRepository.getUser(dto.id())!=null){
            throw new RuntimeException("this user is exist by id"+dto.id());
        }

        User createdUser = new User();
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
