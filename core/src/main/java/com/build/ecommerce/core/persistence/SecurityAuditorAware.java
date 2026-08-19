package com.build.ecommerce.core.persistence;

import com.build.ecommerce.core.security.jwt.auth.JwtAuthenticationToken;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class SecurityAuditorAware implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
            return Optional.empty();
        }

        if (authentication instanceof JwtAuthenticationToken token && token.getPrincipal() instanceof Long id) {
            return Optional.of(String.valueOf(id));
        }

        return Optional.empty();
    }
}
