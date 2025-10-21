package com.mygitgor.repository.impl;

import com.mygitgor.model.User;
import com.mygitgor.repository.UserRepository;

import java.util.HashMap;
import java.util.Map;

public class UserRepositoryImpl implements UserRepository {
    private Map<String, User> map=new HashMap<>();

    @Override
    public User getUser(String userId) {
        return map.get(userId);
    }

    @Override
    public User createUser(User user) {
        return map.put(user.getId(), user);
    }

}
