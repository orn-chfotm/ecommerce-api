
package com.build.ecommerce.core.security.auth.token.login.impl;

import com.build.ecommerce.core.security.exception.AuthorityNotFoundException;
import com.build.ecommerce.core.security.auth.token.login.CustomLoginToken;
import com.build.ecommerce.core.security.auth.service.detail.impl.CustomUserDetails;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

public class CustomAdminLoginToken extends AbstractAuthenticationToken implements CustomLoginToken {

    private final CustomUserDetails customUserDetails;

    private CustomAdminLoginToken(CustomUserDetails customUserDetails, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.customUserDetails = customUserDetails;
    }

    public static CustomAdminLoginToken toUnAuthenticate(String username, String password) {
        return new CustomAdminLoginToken(
                new CustomUserDetails(null, username, password, Collections.emptySet()),
                Collections.emptySet()
        );
    }

    public static CustomAdminLoginToken toAuthenticate(Long userId, Collection<? extends GrantedAuthority> authorities) {
        return new CustomAdminLoginToken(
                new CustomUserDetails(userId, null, null, authorities),
                authorities
        );
    }

    @Override
    public Object getPrincipal() {
        return customUserDetails.getUsername();
    }

    @Override
    public Object getCredentials() {
        return customUserDetails.getPassword();
    }

    @Override
    public Long getId() {
        return customUserDetails.getId();
    }

    @Override
    public String getRole() {
        return getAuthorities().stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElseThrow(AuthorityNotFoundException::new);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        if (!super.equals(object)) return false;
        CustomAdminLoginToken that = (CustomAdminLoginToken) object;
        return Objects.equals(customUserDetails, that.customUserDetails);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), customUserDetails);
    }
}
