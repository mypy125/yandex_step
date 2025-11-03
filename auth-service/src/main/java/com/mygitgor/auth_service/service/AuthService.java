package com.mygitgor.auth_service.service;

import com.mygitgor.auth_service.dto.*;
import com.mygitgor.auth_service.dto.login.LoginRequest;
import com.mygitgor.auth_service.dto.login.SignupRequest;
import com.mygitgor.auth_service.dto.response.AuthResponse;
import com.mygitgor.auth_service.dto.seller.SellerDto;
import com.mygitgor.auth_service.dto.user.UserInfo;
import reactor.core.publisher.Mono;

public interface AuthService {
    Mono<AuthResponse> login(LoginRequest request);
    Mono<Void> requestLoginOtp(String email, USER_ROLE role);
    Mono<AuthResponse> registerSeller(SellerDto request);
    Mono<AuthResponse> registerUser(SignupRequest request, USER_ROLE role);
    Mono<Void> logout(String token);
    Mono<Void> logoutAllDevices(String email);
    Mono<UserInfo> getUserInfoFromToken(String token);
    Mono<Boolean> validateToken(String token);
    Mono<AuthResponse> verifyAndCompleteSellerRegistration(String email, String otp);
}
