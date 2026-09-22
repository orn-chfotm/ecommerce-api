package com.build.ecommerce.userapi.security.login;

import com.build.ecommerce.userapi.security.login.token.CustomUserLoginToken;
import com.build.ecommerce.core.exception.code.ExceptionCode;
import com.build.ecommerce.core.security.exception.extend.AuthenticationFailException;
import com.build.ecommerce.core.security.login.common.detail.impl.CustomUserDetails;
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

        CustomUserDetails details = (CustomUserDetails) userDetailsService.loadUserByUsername(email);

        if (!isPasswordMatches(password, details.getPassword())) {
            throw new AuthenticationFailException(ExceptionCode.AUTHENTICATION_UNAUTHORIZED);
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
