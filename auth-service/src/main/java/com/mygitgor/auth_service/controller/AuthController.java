package com.mygitgor.auth_service.controller;

import com.mygitgor.auth_service.dto.*;
import com.mygitgor.auth_service.dto.response.ApiResponse;
import com.mygitgor.auth_service.dto.response.AuthResponse;
import com.mygitgor.auth_service.service.AuthService;
import com.mygitgor.auth_service.service.VerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final VerificationService verificationService;

    @PostMapping("/otp")
    public Mono<ResponseEntity<ApiResponse>> sendOtp(@RequestBody OtpRequest request
    ) {
        return verificationService.sendOtp(request.getEmail(),request.getRole(),request.getPurpose())
                .map(response -> ResponseEntity.ok(
                        new ApiResponse("OTP sent successfully", true, response)
                ))
                .onErrorResume(e -> Mono.just(
                        ResponseEntity.badRequest().body(
                                new ApiResponse(e.getMessage(), false, null)
                        )
                ));
    }

    @PostMapping("/login")
    public Mono<ResponseEntity<AuthResponse>> login(@RequestBody LoginRequest request, USER_ROLE role
    ) {
        return authService.login(request, role)
                .map(ResponseEntity::ok)
                .onErrorResume(e -> Mono.just(
                        ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
                ));
    }

    @PatchMapping("/verify/{otp}")
    public Mono<ResponseEntity<ApiResponse>> verifyOtp(@PathVariable String otp,
                                                        @RequestBody VerifyOtpRequest request
    ) {
        return verificationService.verifyOtp(otp, request)
                .map(verified -> ResponseEntity.ok(
                        new ApiResponse("Verification successful", true, verified)
                ))
                .onErrorResume(e -> Mono.just(
                        ResponseEntity.badRequest().body(
                                new ApiResponse(e.getMessage(), false, null)
                        )
                ));
    }

    @PostMapping("/logout")
    public Mono<ResponseEntity<ApiResponse>> logout(@RequestHeader("Authorization") String authHeader
    ) {
        String token = extractTokenFromHeader(authHeader);

        return authService.logout(token)
                .thenReturn(ResponseEntity.ok(new ApiResponse("Logout successful", true, null)))
                .onErrorResume(e -> Mono.just(
                        ResponseEntity.badRequest().body(new ApiResponse(e.getMessage(), false, null))
                ));
    }

    @PostMapping("/logout-all")
    public Mono<ResponseEntity<ApiResponse>> logoutAllDevices(@RequestHeader("Authorization") String authHeader
    ) {
        return authService.getUserInfoFromToken(extractTokenFromHeader(authHeader))
                .flatMap(userInfo -> authService.logoutAllDevices(userInfo.getEmail()))
                .thenReturn(ResponseEntity.ok(new ApiResponse("Logged out from all devices", true, null)))
                .onErrorResume(e -> Mono.just(
                        ResponseEntity.badRequest().body(new ApiResponse(e.getMessage(), false, null))
                ));
    }

    @GetMapping("/validate")
    public Mono<ResponseEntity<ApiResponse>> validateToken(@RequestHeader("Authorization") String authHeader
    ) {
        String token = extractTokenFromHeader(authHeader);

        return authService.validateToken(token)
                .map(valid -> {
                    if (valid) {
                        return ResponseEntity.ok(new ApiResponse("Token is valid", true, null));
                    } else {
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                .body(new ApiResponse("Invalid token", false, null));
                    }
                });
    }

    private String extractTokenFromHeader(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        throw new RuntimeException("Invalid authorization header");
    }
}