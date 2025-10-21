package com.mygitgor.repository;

import com.mygitgor.dto.UserDto;
import com.mygitgor.model.User;

public interface UserRepository {
    User getUser(String userId);
    User createUser(User user);
}
