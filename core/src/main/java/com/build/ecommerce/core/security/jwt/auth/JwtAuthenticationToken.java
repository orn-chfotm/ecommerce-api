package com.build.ecommerce.core.security.jwt.auth;

import com.build.ecommerce.core.security.jwt.token.JwtPayload;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

public class JwtAuthenticationToken extends AbstractAuthenticationToken {

    private final Long principal;
    private final String credentials;

    private JwtAuthenticationToken(
            Long principal,
            Collection<? extends GrantedAuthority> authorities,
            String credentials,
            boolean isAuthenticated
    ) {
        super(authorities);
        this.principal = principal;
        this.credentials = credentials;
        setAuthenticated(isAuthenticated);
    }

    public static Authentication toUnAuthenticate(String accessToken) {
        return new JwtAuthenticationToken(null, Collections.emptySet(), accessToken, false);
    }

    public static Authentication toAuthenticate(JwtPayload payload) {
        return new JwtAuthenticationToken(payload.id(), Set.of(new SimpleGrantedAuthority(payload.getRoleSecured())), null, true);
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }

    @Override
    public Object getCredentials() {
        return credentials;
    }
}
