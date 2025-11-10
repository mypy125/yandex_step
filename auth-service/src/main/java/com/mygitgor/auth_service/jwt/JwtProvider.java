package com.mygitgor.auth_service.jwt;

import com.mygitgor.auth_service.client.UserClient;
import com.mygitgor.auth_service.dto.USER_ROLE;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JwtProvider {
    private final JwtProps jwtProps;
    private final UserClient userClient;
    private SecretKey key;

    @PostConstruct
    private void init() {
        this.key = Keys.hmacShaKeyFor(jwtProps.getSecretKey().getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(String email, USER_ROLE role, String userId) {
        return generateToken(email, List.of(role.name()),userId);
    }

    public String generateToken(String email, List<String> authorities,String userId) {
        String roles = String.join(",", authorities);

        return Jwts.builder()
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtProps.getExpirationTime()))
                .claim("email", email)
                .claim("authorities", roles)
                .claim("userId", userId)
                .signWith(key)
                .compact();
    }

    public String generateToken(String email, USER_ROLE role) {
        return userClient.getAuthInfo(email)
                .map(userAuthInfo -> generateToken(email, List.of(role.name()), userAuthInfo.getId()))
                .block();
    }

    public String generateToken(Authentication auth) {
        Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
        String roles = populateAuthorities(authorities);

        return Jwts.builder()
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtProps.getExpirationTime()))
                .claim("email", auth.getName())
                .claim("authorities", roles)
                .signWith(key)
                .compact();
    }

    public String getUserIdFromJwtToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return String.valueOf(claims.get("userId"));
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return false;
        } catch (Exception e) {
            return true;
        }
    }

    public String getEmailFromJwtToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return String.valueOf(claims.get("email"));
    }

    public List<String> getAuthorities(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        String authoritiesStr = String.valueOf(claims.get("authorities"));
        return Arrays.asList(authoritiesStr.split(","));
    }

    public USER_ROLE getRoleFromJwtToken(String token) {
        List<String> authorities = getAuthorities(token);
        if (!authorities.isEmpty()) {
            try {
                return USER_ROLE.valueOf(authorities.get(0));
            } catch (IllegalArgumentException e) {
                return USER_ROLE.ROLE_CUSTOMER;
            }
        }
        return USER_ROLE.ROLE_CUSTOMER;
    }

    public Date extractExpiration(String token) {
        return getClaims(token).getExpiration();
    }

    private String populateAuthorities(Collection<? extends GrantedAuthority> authorities) {
        return authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
    }

    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
