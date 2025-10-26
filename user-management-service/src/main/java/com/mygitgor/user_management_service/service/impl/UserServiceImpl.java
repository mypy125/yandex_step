package com.mygitgor.user_management_service.service.impl;

import com.mygitgor.user_management_service.domain.User;
import com.mygitgor.user_management_service.dto.UserDto;
import com.mygitgor.user_management_service.repository.UserRepository;
import com.mygitgor.user_management_service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserDto findByEmail(String email) {
        User user = userRepository.findByEmail(email);
        if(user != null){
            return new UserDto(
                    user.getId(), user.getFullName(), user.getEmail(), List.of(String.valueOf(user.getRole()))
            );
        }
        throw new IllegalArgumentException("user by email {} not found"+email);
    }


}
