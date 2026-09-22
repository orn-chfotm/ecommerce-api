package com.build.ecommerce.core.security.jwt.auth;

import com.build.ecommerce.core.exception.code.ExceptionCode;
import com.build.ecommerce.core.exception.type.BusinessException;
import com.build.ecommerce.core.security.exception.extend.AuthenticationFailException;
import com.build.ecommerce.core.security.jwt.enums.TokenType;
import com.build.ecommerce.core.security.jwt.exception.TokenException;
import com.build.ecommerce.core.security.jwt.token.JwtPayload;
import com.build.ecommerce.core.security.jwt.token.JwtService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * JwtAuthenticationProvider 단위 테스트.
 * JwtService 검증 실패를 Security 계층 예외(AuthenticationFailException)로 변환할 때
 * 오류 코드와 원인 예외가 그대로 보존되는지 확인한다.
 */
@ExtendWith(MockitoExtension.class)
class JwtAuthenticationProviderTest {

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private JwtAuthenticationProvider jwtAuthenticationProvider;

    @Test
    @DisplayName("토큰이 만료되면 AuthenticationFailException 으로 변환하면서 오류 코드와 원인 예외를 보존한다")
    void authenticate_tokenExpired_preservesErrorCodeAndCause() {
        TokenException cause = new TokenException(ExceptionCode.TOKEN_EXPIRED, new RuntimeException("expired"));
        when(jwtService.verifyToken(anyString())).thenThrow(cause);

        Authentication unAuthenticated = JwtAuthenticationToken.toUnAuthenticate("token");

        assertThatThrownBy(() -> jwtAuthenticationProvider.authenticate(unAuthenticated))
                .isInstanceOf(AuthenticationFailException.class)
                .satisfies(exception -> {
                    AuthenticationFailException failException = (AuthenticationFailException) exception;
                    assertThat(failException.getExceptionCode()).isEqualTo(ExceptionCode.TOKEN_EXPIRED);
                    assertThat(failException.getCause()).isSameAs(cause);
                });
    }

    @Test
    @DisplayName("서명이 올바르지 않으면 AuthenticationFailException 으로 변환하면서 오류 코드와 원인 예외를 보존한다")
    void authenticate_invalidSignature_preservesErrorCodeAndCause() {
        TokenException cause = new TokenException(ExceptionCode.AUTHENTICATION_UNAUTHORIZED, new RuntimeException("invalid signature"));
        when(jwtService.verifyToken(anyString())).thenThrow(cause);

        Authentication unAuthenticated = JwtAuthenticationToken.toUnAuthenticate("token");

        assertThatThrownBy(() -> jwtAuthenticationProvider.authenticate(unAuthenticated))
                .isInstanceOf(AuthenticationFailException.class)
                .satisfies(exception -> {
                    AuthenticationFailException failException = (AuthenticationFailException) exception;
                    assertThat(failException.getExceptionCode()).isEqualTo(ExceptionCode.AUTHENTICATION_UNAUTHORIZED);
                    assertThat(failException.getCause()).isSameAs(cause);
                });
    }

    @Test
    @DisplayName("토큰 이외의 앱 예외는 인증 실패로 변환하지 않는다")
    void authenticate_applicationFailure_propagatesException() {
        BusinessException failure = new BusinessException();
        when(jwtService.verifyToken(anyString())).thenThrow(failure);

        assertThatThrownBy(() -> jwtAuthenticationProvider.authenticate(JwtAuthenticationToken.toUnAuthenticate("token")))
                .isSameAs(failure);
    }

    @Test
    @DisplayName("REFRESH 타입 토큰으로 인증을 시도하면 TOKEN_TYPE_MISMATCH 예외를 던진다")
    void authenticate_refreshTokenType_throwsTokenTypeMismatch() {
        JwtPayload refreshPayload = JwtPayload.builder()
                .tokenType(TokenType.REFRESH)
                .id(1L)
                .authority("USER")
                .issuedAt(new Date())
                .build();
        when(jwtService.verifyToken(anyString())).thenReturn(refreshPayload);

        Authentication unAuthenticated = JwtAuthenticationToken.toUnAuthenticate("token");

        assertThatThrownBy(() -> jwtAuthenticationProvider.authenticate(unAuthenticated))
                .isInstanceOf(AuthenticationFailException.class)
                .satisfies(exception -> {
                    AuthenticationFailException failException = (AuthenticationFailException) exception;
                    assertThat(failException.getExceptionCode()).isEqualTo(ExceptionCode.TOKEN_TYPE_MISMATCH);
                });
    }
}
