package com.mygitgor.repository;

import com.mygitgor.domain.User;
import com.mygitgor.dto.UserDto;

public interface UserRepository {
    User createUser(UserDto dto);
}
