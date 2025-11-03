package com.mygitgor.user_management_service;

import com.mygitgor.user_management_service.domain.USER_ROLE;
import com.mygitgor.user_management_service.domain.User;
import com.mygitgor.user_management_service.dto.SignupRequest;
import com.mygitgor.user_management_service.dto.UserDto;
import com.mygitgor.user_management_service.mapper.UserMapper;
import com.mygitgor.user_management_service.repository.UserRepository;
import com.mygitgor.user_management_service.service.impl.UserServiceImpl;
import com.sun.jdi.request.DuplicateRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;
    private UserDto testUserDto;
    private SignupRequest testSignupRequest;

    @BeforeEach
    public void setUp() {
        testUser = new User();
        testUser.setId(UUID.randomUUID());
        testUser.setEmail("test@example.com");
        testUser.setFullName("Test User");
        testUser.setPassword("encodedPassword");
        testUser.setRole(USER_ROLE.ROLE_CUSTOMER);
        testUser.setCreatedAt(LocalDateTime.now());
        testUser.setUpdatedAt(LocalDateTime.now());

        testUserDto = UserDto.builder()
                .id(testUser.getId())
                .email(testUser.getEmail())
                .fullName(testUser.getFullName())
                .role(testUser.getRole())
                .createdAt(testUser.getCreatedAt())
                .updatedAt(testUser.getUpdatedAt())
                .build();

        testSignupRequest = new SignupRequest();
        testSignupRequest.setEmail("newuser@example.com");
        testSignupRequest.setFullName("New User");
        testSignupRequest.setOtp("123456");
    }

    @Test
    void findUserByEmail_IfExists_ReturnUserDto(){
        String email = "test@example.com";
        when(userRepository.findByEmail(email))
                .thenReturn(Optional.of(testUser));

        UserDto result = userService.findByEmail(email);

        assertNotNull(result);
        assertEquals(testUser.getId(), result.getId());
        assertEquals(testUser.getEmail(), result.getEmail());
        assertEquals(testUser.getFullName(), result.getFullName());
        assertEquals(testUser.getRole(), result.getRole());

        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    void findUserByEmail_ShouldException_WhenUserNotFound() {
        String email = "nonexistent@example.com";
        when(userRepository.findByEmail(email))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userService.findByEmail(email));

        assertEquals("User not found " + email, exception.getMessage());
        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    void createUserFromAuthRequest_ShouldCreateUser_WhenEmailNotExists() {
        when(userRepository.findByEmail(testSignupRequest.getEmail()))
                .thenReturn(Optional.empty());
        when(passwordEncoder.encode(testSignupRequest.getOtp()))
                .thenReturn("encodedPassword");
        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> {
                    User user = invocation.getArgument(0);
                    user.setId(UUID.randomUUID());
                    return user;
                });

        UserDto expectedDto = UserDto.builder()
                .email(testSignupRequest.getEmail())
                .fullName(testSignupRequest.getFullName())
                .role(USER_ROLE.ROLE_CUSTOMER)
                .build();

        when(userMapper.toUserDto(any(User.class))).thenReturn(expectedDto);

        UserDto result = userService.createUser(testSignupRequest);

        assertNotNull(result);
        assertEquals(testSignupRequest.getEmail(), result.getEmail());
        assertEquals(testSignupRequest.getFullName(), result.getFullName());
        assertEquals(USER_ROLE.ROLE_CUSTOMER, result.getRole());

        verify(userRepository, times(1)).findByEmail(testSignupRequest.getEmail());
        verify(passwordEncoder, times(1)).encode(testSignupRequest.getOtp());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void createUserFromAuthRequest_ShouldThrowException_WhenEmailAlreadyExists() {
        when(userRepository.findByEmail(testSignupRequest.getEmail()))
                .thenReturn(Optional.of(testUser));

        DuplicateRequestException exception = assertThrows(DuplicateRequestException.class,
                () -> userService.createUser(testSignupRequest));

        assertEquals(String.format("user with email '%s' already exist", testSignupRequest.getEmail()),
                exception.getMessage()
        );

        verify(userRepository, times(1)).findByEmail(testSignupRequest.getEmail());
        verify(userRepository, never()).save(any(User.class));
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    void createUserByUserDto_ShouldCreateUser_WithUserMapper() {
        when(userMapper.toUser(testUserDto))
                .thenReturn(testUser);
        when(userRepository.save(testUser))
                .thenReturn(testUser);
        when(userMapper.toUserDto(testUser))
                .thenReturn(testUserDto);

        UserDto result = userService.createUser(testUserDto);

        assertNotNull(result);
        assertEquals(testUserDto.getId(), result.getId());
        assertEquals(testUserDto.getEmail(), result.getEmail());
        assertEquals(testUserDto.getFullName(), result.getFullName());

        verify(userMapper, times(1)).toUser(testUserDto);
        verify(userRepository, times(1)).save(testUser);
        verify(userMapper, times(1)).toUserDto(testUser);
    }

    @Test
    void createUserByUserDto_ShouldHandleNullFields() {
        UserDto partialUserDto = UserDto.builder()
                .email("partial@example.com")
                .fullName("Partial User")
                .build();

        User partialUser = new User();
        partialUser.setEmail(partialUserDto.getEmail());
        partialUser.setFullName(partialUserDto.getFullName());

        when(userMapper.toUser(partialUserDto))
                .thenReturn(partialUser);
        when(userRepository.save(partialUser))
                .thenReturn(partialUser);
        when(userMapper.toUserDto(partialUser))
                .thenReturn(partialUserDto);

        UserDto result = userService.createUser(partialUserDto);

        assertNotNull(result);
        assertEquals(partialUserDto.getEmail(), result.getEmail());
        assertEquals(partialUserDto.getFullName(), result.getFullName());

        verify(userMapper, times(1)).toUser(partialUserDto);
        verify(userRepository, times(1)).save(partialUser);
    }


}
