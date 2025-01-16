package com.build.ecommerce.core.security.auth.privider;

import com.build.ecommerce.core.security.exception.AuthenticationFailException;
import com.build.ecommerce.core.security.auth.service.detail.impl.CustomUserDetails;
import com.build.ecommerce.core.security.auth.token.login.impl.CustomUserLoginToken;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

@RequiredArgsConstructor
public class CustomUserLoginProvider implements AuthenticationProvider {

    private final PasswordEncoder passwordEncoder;
    private final UserDetailsService userDetailsService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String email = (String) authentication.getPrincipal();
        String password = (String) authentication.getCredentials();

        CustomUserDetails details = (CustomUserDetails)userDetailsService.loadUserByUsername(email);

        if (!isPasswordMatches(password, details.getPassword())) {
            throw new AuthenticationFailException("비밀번호가 일치하지 않습니다.");
        }

        return CustomUserLoginToken.toAuthenticate(
                details.getId(),
                details.getAuthorities()
        );
    }

    private boolean isPasswordMatches(final String rawPassword, final String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return CustomUserLoginToken.class.isAssignableFrom(authentication);
    }
}
