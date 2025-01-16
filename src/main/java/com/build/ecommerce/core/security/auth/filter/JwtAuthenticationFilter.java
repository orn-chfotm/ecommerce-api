package com.build.ecommerce.core.security.auth.filter;

import com.build.ecommerce.core.security.auth.token.jwt.JwtAuthenticationToken;
import com.build.ecommerce.core.config.property.JwtProperty;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final AuthenticationManager authenticationManager;
    private final JwtProperty jwtProperty;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (!hasJwtToken(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        String accessToken = request.getHeader(HttpHeaders.AUTHORIZATION).substring(jwtProperty.getTokenType().length() + 1);

        SecurityContextHolder.getContext()
                .setAuthentication(authenticationManager.authenticate(
                        JwtAuthenticationToken.toUnAuthenticate(accessToken)
                ));

        filterChain.doFilter(request, response);
    }

    private boolean hasJwtToken(HttpServletRequest request) {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        return header != null && header.startsWith(jwtProperty.getTokenType() + " ");
    }
}
