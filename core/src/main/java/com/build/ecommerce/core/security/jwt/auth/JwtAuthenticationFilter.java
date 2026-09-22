package com.build.ecommerce.core.security.jwt.auth;

import com.build.ecommerce.core.security.jwt.property.JwtProperty;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final AuthenticationManager authenticationManager;
    private final JwtProperty jwtProperty;
    private final AuthenticationEntryPoint authenticationEntryPoint;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (!hasJwtToken(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        String accessToken = request.getHeader(HttpHeaders.AUTHORIZATION).substring(jwtProperty.getTokenType().length() + 1);

        try {
            SecurityContextHolder.getContext()
                    .setAuthentication(authenticationManager.authenticate(
                            JwtAuthenticationToken.toUnAuthenticate(accessToken)
                    ));
        } catch (AuthenticationException e) {
            // 인증 실패 응답은 EntryPoint 가 쓴다. 이후 필터 체인은 진행하지 않는다.
            SecurityContextHolder.clearContext();
            authenticationEntryPoint.commence(request, response, e);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean hasJwtToken(HttpServletRequest request) {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        return header != null && header.startsWith(jwtProperty.getTokenType() + " ");
    }
}
