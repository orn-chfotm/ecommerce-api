package com.build.ecommerce.core.security.jwt.auth;

import com.build.ecommerce.core.exception.code.ExceptionCode;
import com.build.ecommerce.core.security.exception.extend.AuthenticationFailException;
import com.build.ecommerce.core.security.jwt.enums.TokenType;
import com.build.ecommerce.core.security.jwt.exception.TokenException;
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

        JwtPayload jwtDto;
        try {
            jwtDto = jwtService.verifyToken(jwtToken);
        } catch (TokenException e) {
            // 토큰 검증 실패를 Security 계층 계약(AuthenticationException)으로 변환한다. 코드와 원인 예외를 보존한다.
            throw new AuthenticationFailException(e.getErrorCode(), e);
        }

        if (jwtDto.tokenType() != TokenType.ACCESS) {
            throw new AuthenticationFailException(ExceptionCode.TOKEN_TYPE_MISMATCH);
        }

        return JwtAuthenticationToken.toAuthenticate(jwtDto);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.isAssignableFrom(JwtAuthenticationToken.class);
    }
}
