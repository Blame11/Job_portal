package com.jobportal.apigateway.filter;

import com.jobportal.apigateway.constants.GatewayConstants;
import com.jobportal.apigateway.security.JwtValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;

@Slf4j
@Component
public class JwtValidationFilter extends AbstractGatewayFilterFactory<JwtValidationFilter.Config> {

    private final JwtValidator jwtValidator;

    public JwtValidationFilter(JwtValidator jwtValidator) {
        super(Config.class);
        this.jwtValidator = jwtValidator;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            String path = request.getPath().toString();

            // Skip JWT validation for public endpoints
            if (isPublicPath(path)) {
                return chain.filter(exchange);
            }

            // Extract JWT from cookie
            String token = extractTokenFromCookie(request);

            if (token == null || token.isEmpty()) {
                log.warn("Missing JWT token for path: {}", path);
                return onError(exchange, HttpStatus.UNAUTHORIZED);
            }

            // Validate JWT
            if (!jwtValidator.isTokenValid(token)) {
                log.warn("Invalid JWT token for path: {}", path);
                return onError(exchange, HttpStatus.UNAUTHORIZED);
            }

            // Extract userId and role from token
            String userId = jwtValidator.getUserIdFromToken(token);
            String role = jwtValidator.getRoleFromToken(token);

            if (userId == null || role == null) {
                log.warn("Failed to extract userId/role from token");
                return onError(exchange, HttpStatus.UNAUTHORIZED);
            }

            // Add headers to request
            ServerHttpRequest modifiedRequest = request.mutate()
                    .header(GatewayConstants.HEADER_USER_ID, userId)
                    .header(GatewayConstants.HEADER_USER_ROLE, role)
                    .build();

            ServerWebExchange modifiedExchange = exchange.mutate().request(modifiedRequest).build();
            return chain.filter(modifiedExchange);
        };
    }

    private boolean isPublicPath(String path) {
        return Arrays.stream(GatewayConstants.PUBLIC_PATHS)
                .anyMatch(path::startsWith);
    }

    private String extractTokenFromCookie(ServerHttpRequest request) {
        var cookies = request.getCookies().get(GatewayConstants.COOKIE_NAME);
        if (cookies != null && !cookies.isEmpty()) {
            return cookies.get(0).getValue();
        }
        return null;
    }

    private Mono<Void> onError(ServerWebExchange exchange, HttpStatus status) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        response.getHeaders().add("Content-Type", "application/json");
        String body = "{\"status\": false, \"message\": \"" + status.getReasonPhrase() + "\"}";
        return response.writeWith(Mono.just(response.bufferFactory().wrap(body.getBytes())));
    }

    public static class Config {
    }
}
