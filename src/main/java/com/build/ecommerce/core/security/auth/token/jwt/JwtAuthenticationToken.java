package com.build.ecommerce.core.security.auth.token.jwt;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Set;

public class JwtAuthenticationToken extends AbstractAuthenticationToken {

    private final Long principal;
    private final String credentials;
    private final boolean isAuthenticated;

    private JwtAuthenticationToken(Long principal, String credentials, boolean isAuthenticated) {
        super(Set.of(new SimpleGrantedAuthority("ROLE_USER")));
        this.principal = principal;
        this.credentials = credentials;
        this.isAuthenticated = isAuthenticated;
    }

    public static Authentication toUnAuthenticate(String accessToken) {
        return new JwtAuthenticationToken(null, accessToken, false);
    }

    public static Authentication toAuthenticate(Long id) {
        return new JwtAuthenticationToken(id, null, true);
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }

    @Override
    public Object getCredentials() {
        return credentials;
    }

    @Override
    public boolean isAuthenticated() {
        return isAuthenticated;
    }
}
