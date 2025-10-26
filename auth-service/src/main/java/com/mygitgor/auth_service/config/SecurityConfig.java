package com.mygitgor.auth_service.config;

import com.mygitgor.auth_service.jwt.JwtProps;
import com.mygitgor.auth_service.jwt.JwtProvider;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfig {
    private final JwtProvider jwtProvider;
    private final JwtProps jwtProps;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        AuthenticationWebFilter jwtWebFilter = new AuthenticationWebFilter(reactiveAuthenticationManager());
        jwtWebFilter.setServerAuthenticationConverter(jwtAuthenticationConverter());

        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeExchange(exchange -> exchange
                        .pathMatchers("/auth/**").permitAll()
                        .pathMatchers(HttpMethod.OPTIONS).permitAll()
                        .anyExchange().authenticated()
                )
                .addFilterAt(corsWebFilter(), SecurityWebFiltersOrder.CORS)
                .addFilterAt(jwtWebFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .build();
    }

    @Bean
    public ReactiveAuthenticationManager reactiveAuthenticationManager() {
        return authentication -> Mono.just(authentication);
    }

    @Bean
    public ServerAuthenticationConverter jwtAuthenticationConverter() {
        return exchange -> {
            try {
                String authHeader = exchange.getRequest().getHeaders().getFirst(jwtProps.getHeader());
                if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                    return Mono.empty();
                }

                String token = authHeader.substring(7);
                if (!jwtProvider.validateToken(token)) {
                    return Mono.empty();
                }

                String email = jwtProvider.getEmailFromJwtToken(token);
                List<SimpleGrantedAuthority> authorities = jwtProvider.getAuthorities(token)
                        .stream()
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

                Authentication auth = new UsernamePasswordAuthenticationToken(
                        email, null, authorities);
                return Mono.just(auth);
            } catch (Exception e) {
                return Mono.empty();
            }
        };
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(
                "http://localhost:3000",
                "https://ecommerce-multivendor-frontend.onrender.com",
                "https://ecommerce-multivendor-frontend-ijkm.onrender.com"
        ));
        configuration.setAllowedMethods(List.of(
                HttpMethod.GET.name(),
                HttpMethod.POST.name(),
                HttpMethod.PUT.name(),
                HttpMethod.PATCH.name(),
                HttpMethod.DELETE.name(),
                HttpMethod.OPTIONS.name()
        ));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setExposedHeaders(List.of("Authorization"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public CorsWebFilter corsWebFilter() {
        return new CorsWebFilter(corsConfigurationSource());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}