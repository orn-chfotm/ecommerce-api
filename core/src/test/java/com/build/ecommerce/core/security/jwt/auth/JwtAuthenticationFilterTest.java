package com.build.ecommerce.core.security.jwt.auth;

import com.build.ecommerce.core.exception.code.ExceptionCode;
import com.build.ecommerce.core.security.exception.extend.AuthenticationFailException;
import com.build.ecommerce.core.security.jwt.property.JwtProperty;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * JwtAuthenticationFilter 단위 테스트.
 * 인증 실패 시 EntryPoint 로 응답을 위임하고 이후 필터 체인은 진행하지 않는지,
 * 인증 성공/헤더 없음 케이스에서는 필터 체인이 정상 진행되는지 확인한다.
 */
@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtProperty jwtProperty;
    @Mock
    private AuthenticationEntryPoint authenticationEntryPoint;
    @Mock
    private FilterChain filterChain;

    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @BeforeEach
    void setUp() {
        jwtAuthenticationFilter = new JwtAuthenticationFilter(authenticationManager, jwtProperty, authenticationEntryPoint);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("인증에 실패하면 EntryPoint 가 응답을 쓰고 이후 필터 체인은 진행하지 않으며 SecurityContext 를 비운다")
    void doFilter_authenticationFails_commencesEntryPointAndStopsChain() throws Exception {
        when(jwtProperty.getTokenType()).thenReturn("Bearer");
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer some.token");
        MockHttpServletResponse response = new MockHttpServletResponse();

        AuthenticationException authenticationException = new AuthenticationFailException(ExceptionCode.TOKEN_EXPIRED);
        when(authenticationManager.authenticate(any())).thenThrow(authenticationException);
        SecurityContextHolder.getContext().setAuthentication(mock(Authentication.class));

        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        verify(authenticationEntryPoint).commence(request, response, authenticationException);
        verify(filterChain, never()).doFilter(any(), any());
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    @DisplayName("인증에 성공하면 SecurityContext 를 채우고 다음 필터 체인을 진행한다")
    void doFilter_authenticationSucceeds_proceedsFilterChain() throws Exception {
        when(jwtProperty.getTokenType()).thenReturn("Bearer");
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer some.token");
        MockHttpServletResponse response = new MockHttpServletResponse();

        Authentication authenticated = mock(Authentication.class);
        when(authenticationManager.authenticate(any())).thenReturn(authenticated);

        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verify(authenticationEntryPoint, never()).commence(any(), any(), any());
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isSameAs(authenticated);
    }

    @Test
    @DisplayName("후속 필터의 인증 예외는 JWT 필터의 EntryPoint가 처리하지 않는다")
    void doFilter_downstreamFailure_propagatesException() throws Exception {
        when(jwtProperty.getTokenType()).thenReturn("Bearer");
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer some.token");
        MockHttpServletResponse response = new MockHttpServletResponse();
        when(authenticationManager.authenticate(any())).thenReturn(mock(Authentication.class));
        AuthenticationException failure = new AuthenticationFailException(ExceptionCode.AUTHENTICATION_UNAUTHORIZED);
        doThrow(failure).when(filterChain).doFilter(request, response);

        assertThatThrownBy(() -> jwtAuthenticationFilter.doFilter(request, response, filterChain))
                .isSameAs(failure);

        verify(authenticationEntryPoint, never()).commence(any(), any(), any());
    }

    @Test
    @DisplayName("Authorization 헤더가 없으면 인증을 시도하지 않고 다음 필터 체인만 진행한다")
    void doFilter_noAuthorizationHeader_skipsAuthenticationAndProceeds() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        verify(authenticationManager, never()).authenticate(any());
        verify(filterChain).doFilter(request, response);
    }
}
