package com.build.ecommerce.core.security.jwt.auth;

import com.build.ecommerce.core.security.exception.extend.AuthenticationFailException;
import com.build.ecommerce.core.security.jwt.enums.TokenType;
import com.build.ecommerce.core.security.jwt.token.JwtPayload;
import com.build.ecommerce.core.security.jwt.token.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

@RequiredArgsConstructor
public class JwtAuthenticationProvider implements AuthenticationProvider {

    private final JwtService jwtService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String jwtToken = (String) authentication.getCredentials();

        JwtPayload jwtDto = jwtService.verifyToken(jwtToken);

        if (jwtDto.tokenType() != TokenType.ACCESS) {
            throw new AuthenticationFailException("Access Token이 아닙니다.");
        }

        return JwtAuthenticationToken.toAuthenticate(jwtDto);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.isAssignableFrom(JwtAuthenticationToken.class);
    }
}
