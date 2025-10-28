package com.mygitgor.user_management_service.service.impl;

import com.mygitgor.user_management_service.domain.USER_ROLE;
import com.mygitgor.user_management_service.domain.User;
import com.mygitgor.user_management_service.dto.SignupRequest;
import com.mygitgor.user_management_service.dto.UserDto;
import com.mygitgor.user_management_service.repository.UserRepository;
import com.mygitgor.user_management_service.service.NotificationService;
import com.mygitgor.user_management_service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final NotificationService notificationService;

    @Override
    public UserDto findByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found " + email));

        return new UserDto(user.getId(), user.getEmail(), user.getFullName(),List.of(user.getRole()));
    }

    @Override
    public UserDto createUser(SignupRequest req) {
        userRepository.findByEmail(req.email()).ifPresent(u -> {
            throw new RuntimeException("User already exists with email " + req.email());
        });

        User user = new User();
        user.setEmail(req.email());
        user.setFullName(req.fullName());
        user.setPassword(passwordEncoder.encode(req.otp()));
        user.setRole(USER_ROLE.ROLE_CUSTOMER);

        userRepository.save(user);
        //TODO: cart-client createUserCart(userId)
        notificationService.sendOtpToNotificationService(req.email(),req.otp());
        return new UserDto(user.getId(), user.getEmail(), user.getFullName(), List.of(user.getRole()));
    }


}
