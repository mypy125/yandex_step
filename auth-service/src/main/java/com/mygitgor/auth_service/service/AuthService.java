package com.mygitgor.auth_service.service;

import com.mygitgor.auth_service.dto.*;
import com.mygitgor.auth_service.dto.response.AuthResponse;
import com.mygitgor.auth_service.dto.user.UserInfo;
import reactor.core.publisher.Mono;

public interface AuthService {
    Mono<AuthResponse> login(LoginRequest request, USER_ROLE role);
    Mono<Void> logout(String token);
    Mono<Void> logoutAllDevices(String email);
    Mono<AuthResponse> registerUser(SignupRequest request, USER_ROLE role);
    Mono<UserInfo> getUserInfoFromToken(String token);
    Mono<Boolean> validateToken(String token);

}
