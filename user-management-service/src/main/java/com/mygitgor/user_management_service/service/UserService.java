package com.mygitgor.user_management_service.service;

import com.mygitgor.user_management_service.dto.SignupRequest;
import com.mygitgor.user_management_service.dto.UserDto;

public interface UserService {
    UserDto findByEmail(String email);
    UserDto createUser(SignupRequest request);
    UserDto createUser(UserDto req);
}
