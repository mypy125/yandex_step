package com.mygitgor.seller_service.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtUtils {

    public String extractUserId(String token) {
        try {
            String jwtToken = token.replace("Bearer ", "");
            DecodedJWT decodedJWT = JWT.decode(jwtToken);

            String userId = decodedJWT.getClaim("userId").asString();

            if (userId == null) {
                log.warn("userId claim not found in JWT token");
                throw new IllegalArgumentException("User ID not found in token");
            }

            log.debug("Extracted userId: {} from JWT", userId);
            return userId;

        } catch (Exception e) {
            log.error("Failed to extract userId from JWT token: {}", e.getMessage());
            throw new IllegalArgumentException("Invalid JWT token: " + e.getMessage());
        }
    }

    public String extractEmail(String token) {
        try {
            String jwtToken = token.replace("Bearer ", "");
            DecodedJWT decodedJWT = JWT.decode(jwtToken);

            String email = decodedJWT.getClaim("email").asString();
            if (email == null) {
                email = decodedJWT.getSubject();
            }

            if (email == null) {
                throw new IllegalArgumentException("Email not found in token");
            }

            return email;

        } catch (Exception e) {
            log.error("Failed to extract email from JWT token: {}", e.getMessage());
            throw new IllegalArgumentException("Invalid JWT token: " + e.getMessage());
        }
    }

    public String extractRole(String token) {
        try {
            String jwtToken = token.replace("Bearer ", "");
            DecodedJWT decodedJWT = JWT.decode(jwtToken);

            String authorities = decodedJWT.getClaim("authorities").asString();
            if (authorities != null && !authorities.isEmpty()) {
                return authorities.split(",")[0];
            }

            throw new IllegalArgumentException("Authorities not found in token");

        } catch (Exception e) {
            log.error("Failed to extract role from JWT token: {}", e.getMessage());
            throw new IllegalArgumentException("Invalid JWT token: " + e.getMessage());
        }
    }

    public Map<String, Object> extractAllClaims(String token) {
        try {
            String jwtToken = token.replace("Bearer ", "");
            DecodedJWT decodedJWT = JWT.decode(jwtToken);

            Map<String, Object> claims = new HashMap<>();
            claims.put("userId", decodedJWT.getClaim("userId").asString());
            claims.put("email", decodedJWT.getClaim("email").asString());
            claims.put("authorities", decodedJWT.getClaim("authorities").asString());
            claims.put("issuedAt", decodedJWT.getIssuedAt());
            claims.put("expiresAt", decodedJWT.getExpiresAt());

            return claims;

        } catch (Exception e) {
            log.error("Failed to extract claims from JWT token: {}", e.getMessage());
            throw new IllegalArgumentException("Invalid JWT token: " + e.getMessage());
        }
    }

    public boolean isTokenValid(String token) {
        try {
            String jwtToken = token.replace("Bearer ", "");
            DecodedJWT decodedJWT = JWT.decode(jwtToken);

            Date expiresAt = decodedJWT.getExpiresAt();
            if (expiresAt != null && expiresAt.before(new Date())) {
                log.warn("JWT token has expired");
                return false;
            }

            String userId = decodedJWT.getClaim("userId").asString();
            String email = decodedJWT.getClaim("email").asString();

            return userId != null && email != null;

        } catch (Exception e) {
            log.error("JWT token is invalid: {}", e.getMessage());
            return false;
        }
    }
}
