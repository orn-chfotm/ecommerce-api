package com.build.ecommerce.core.security.auth.privider;

import com.build.ecommerce.core.security.exception.AuthenticationFailException;
import com.build.ecommerce.core.security.auth.service.detail.impl.CustomUserDetails;
import com.build.ecommerce.core.security.auth.token.login.impl.CustomAdminLoginToken;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

@RequiredArgsConstructor
public class CustomAdminLoginProvider implements AuthenticationProvider {

    private final PasswordEncoder passwordEncoder;
    private final UserDetailsService userDetailsService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String email = (String) authentication.getPrincipal();
        String password = (String) authentication.getCredentials();

        CustomUserDetails details = (CustomUserDetails) userDetailsService.loadUserByUsername(email);

        if (!passwordEncoder.matches(password, details.getPassword())) {
            throw new AuthenticationFailException("비밀번호가 일치하지 않습니다.");
        }

        return CustomAdminLoginToken.toAuthenticate(
                details.getId(),
                details.getAuthorities()
        );
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return CustomAdminLoginToken.class.isAssignableFrom(authentication);
    }
}
