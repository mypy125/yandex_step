package com.mygitgor.order_service.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;


@Service
@RequiredArgsConstructor
public class JwtUtils {
    private final JwtProps jwtProps;
    private SecretKey key;

    @PostConstruct
    private void init() {
        this.key = Keys.hmacShaKeyFor(jwtProps.getSecretKey().getBytes(StandardCharsets.UTF_8));
    }

    public String extractUserIdFromJwt(String jwt) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(jwt)
                .getBody();
        return String.valueOf(claims.get("id"));
    }
}
