package com.mygitgor.user_management_service.service.impl;

import com.mygitgor.user_management_service.domain.USER_ROLE;
import com.mygitgor.user_management_service.domain.User;
import com.mygitgor.user_management_service.dto.SignupRequest;
import com.mygitgor.user_management_service.dto.UserDto;
import com.mygitgor.user_management_service.mapper.UserMapper;
import com.mygitgor.user_management_service.repository.UserRepository;
import com.mygitgor.user_management_service.service.NotificationService;
import com.mygitgor.user_management_service.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final NotificationService notificationService;
    private final UserMapper userMapper;

    @Override
    public UserDto findByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found " + email));

        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole())
                .build();
    }

    @Override
    public UserDto createUser(SignupRequest req) {
        userRepository.findByEmail(req.getEmail()).ifPresent(u -> {
            throw new RuntimeException("User already exists with email " + req.getEmail());
        });

        User user = new User();
        user.setEmail(req.getEmail());
        user.setFullName(req.getFullName());
        user.setPassword(passwordEncoder.encode(req.getOtp()));
        user.setRole(USER_ROLE.ROLE_CUSTOMER);
        userRepository.save(user);

        notificationService.sendOtpToNotificationService(req.getEmail(),req.getOtp());
        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole())
                .build();
    }

    @Override
    public UserDto createUser(UserDto req) {
        User user = userMapper.toUser(req);
        userRepository.save(user);
        return userMapper.toUserDto(user);
    }


}
