package com.mygitgor.auth_service.service;

import com.mygitgor.auth_service.client.UserClient;
import com.mygitgor.auth_service.dto.SignupRequest;
import com.mygitgor.auth_service.dto.UserDto;
import com.mygitgor.auth_service.jwt.JwtProvider;
import com.mygitgor.auth_service.utils.OtpUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserClient userClient;
    private final JwtProvider jwtProvider;
    private final OtpSenderService otpSenderService;

    @Override
    public void sendLoginOtp(String email) {
        isValidEmailFormat(email);
        try{
            String otp = OtpUtil.generateOtp();
            userClient.saveVerificationCode(email, otp);
            otpSenderService.sendOtpToNotificationService(email,otp);
            log.info("OTP sent for {}", email);
        }catch (Exception e){
            log.error("Failed to send OTP for {}", email, e);
            throw new RuntimeException("Failed to send OTP: " + e.getMessage());
        }

    }

    @Override
    public String verifyOtpAndLogin(String email, String otp) {
        final String method = "verifyOtpAndLogin";
        validateEmailAndOtp(email, otp, method);

        try {
            boolean valid = userClient.verifyOtp(email, otp);
            if (!valid) {
                throw new RuntimeException("Invalid OTP");
            }
            log.debug("{}: OTP verified successfully for email: {}", method, email);

            log.debug("{}: Retrieving user by email: {}", method, email);
            UserDto user = userClient.getUserByEmail(email);
            if (user == null) {
                log.error("{}: User not found for email: {}", method, email);
                throw new RuntimeException("User not found");
            }

            log.debug("{}: User retrieved successfully. ID: {}, Roles: {}",
                    method, user.id(), user.getRoles());
            return jwtProvider.generateToken(user.email(), user.getRoles());

            //TODO: catching token

        } catch (RuntimeException e) {
            log.error("{}: Login failed for email: {}. Error: {}",
                    method, email, e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Authentication failed " + e.getMessage(), e);
        }
    }

    @Override
    public String createUser(SignupRequest request) {
        final String method = "createUser";
        validateSignupRequest(request, method);

        boolean valid = userClient.verifyOtp(request.email(),request.otp());

        if (!valid) {
            throw new RuntimeException("Invalid OTP");
        }

        UserDto user = userClient.createUserInUserService(request);

        List<SimpleGrantedAuthority> authorities = user.getRoles().stream()
                .map(SimpleGrantedAuthority::new)
                .toList();

        Authentication authentication = new UsernamePasswordAuthenticationToken(user.email(), null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return jwtProvider.generateToken(authentication);
        //TODO: saving jwt token in db auth-service__and__adding_cache-rds
    }


    private void validateSignupRequest(SignupRequest req, String method) {
        if (req == null) {
            log.error("{}: SignupRequest is null", method);
            throw new IllegalArgumentException("Signup request cannot be null");
        }

        validateEmailAndOtp(req.email(), req.otp(), method);
    }

    private void validateEmailAndOtp(String email, String otp, String method) {
        if (!StringUtils.hasText(email) || !StringUtils.hasText(otp)) {
            log.error("{}: Invalid input parameters - email or OTP is empty. Email: {}",
                    method, email);
            throw new IllegalArgumentException("Email and OTP are required");
        }

        if (!isValidEmailFormat(email)) {
            log.error("{}: Invalid email format: {}", method, email);
            throw new IllegalArgumentException("Invalid email format");
        }
    }

    private boolean isValidEmailFormat(String email) {
        if (!StringUtils.hasText(email)) {
            return false;
        }
        return email.contains("@") && email.length() > 3;
    }
}
