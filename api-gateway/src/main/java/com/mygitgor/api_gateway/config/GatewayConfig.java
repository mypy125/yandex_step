package com.mygitgor.api_gateway.config;

import com.mygitgor.api_gateway.filter.AuthenticationFilter;
import com.mygitgor.api_gateway.filter.LoggingFilter;
import com.mygitgor.api_gateway.filter.RateLimitingFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class GatewayConfig {
    private final AuthenticationFilter authenticationFilter;
    private final LoggingFilter loggingFilter;
    private final RateLimitingFilter rateLimitingFilter;

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("auth-service", r -> r.path("/api/auth/**")
                        .filters(f -> f
                                .filter(rateLimitingFilter.apply(
                                        new RateLimitingFilter.Config(20, 40)
                                ))
                                .filter(loggingFilter)
                                .stripPrefix(1)
                        )
                        .uri("lb://auth-service")
                )
                .route("user-service", r -> r.path("/api/users/**", "/api/addresses/**")
                        .filters(f -> f
                                .filter(authenticationFilter)
                                .filter(rateLimitingFilter.apply(
                                        new RateLimitingFilter.Config(15, 30)
                                ))
                                .filter(loggingFilter)
                                .stripPrefix(1)
                        )
                        .uri("lb://user-service")
                )
                .route("product-service-public", r -> r
                        .path("/api/products", "/api/products/{id}", "/api/categories", "/api/products/*/reviews")
                        .and().method("GET")
                        .filters(f -> f
                                .filter(rateLimitingFilter.apply(
                                        new RateLimitingFilter.Config(30, 60)
                                ))
                                .filter(loggingFilter)
                                .stripPrefix(1)
                        )
                        .uri("lb://product-service")
                )
                .route("product-service-secure", r -> r
                        .path("/api/products/**", "/api/categories/**")
                        .and().method("POST", "PUT", "DELETE", "PATCH")
                        .filters(f -> f
                                .filter(authenticationFilter)
                                .filter(rateLimitingFilter.apply(
                                        new RateLimitingFilter.Config(10, 20)
                                ))
                                .filter(loggingFilter)
                                .stripPrefix(1)
                        )
                        .uri("lb://product-service")
                )
                .route("order-service", r -> r.path("/api/orders/**", "/api/cart/**")
                        .filters(f -> f
                                .filter(authenticationFilter)
                                .filter(rateLimitingFilter.apply(
                                        new RateLimitingFilter.Config(10, 20)
                                ))
                                .filter(loggingFilter)
                                .stripPrefix(1)
                        )
                        .uri("lb://order-service")
                )
                .route("payment-service-public", r -> r.path("/api/webhooks/**")
                        .filters(f -> f
                                .filter(rateLimitingFilter.apply(
                                        new RateLimitingFilter.Config(50, 100)
                                ))
                                .filter(loggingFilter)
                                .stripPrefix(1)
                        )
                        .uri("lb://payment-service")
                )
                .route("payment-service-secure", r -> r.path("/api/payments/**")
                        .filters(f -> f
                                .filter(authenticationFilter)
                                .filter(rateLimitingFilter.apply(
                                        new RateLimitingFilter.Config(20, 40)
                                ))
                                .filter(loggingFilter)
                                .stripPrefix(1)
                        )
                        .uri("lb://payment-service")
                )
                .route("seller-service", r -> r.path("/api/sellers/**", "/api/vendor/**")
                        .filters(f -> f
                                .filter(authenticationFilter)
                                .filter(rateLimitingFilter.apply(
                                        new RateLimitingFilter.Config(10, 20)
                                ))
                                .filter(loggingFilter)
                                .stripPrefix(1)
                        )
                        .uri("lb://seller-service")
                )
                .route("fallback-route", r -> r.path("/fallback/**")
                        .filters(f -> f.setPath("/api/gateway/fallback"))
                        .uri("http://localhost:8080")
                )
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(
                "http://localhost:3000",
                "https://ecommerce-multivendor-frontend.onrender.com",
                "https://ecommerce-multivendor-frontend-ijkm.onrender.com"
        ));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setExposedHeaders(Arrays.asList("Authorization", "X-Request-ID", "X-RateLimit-Remaining"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}