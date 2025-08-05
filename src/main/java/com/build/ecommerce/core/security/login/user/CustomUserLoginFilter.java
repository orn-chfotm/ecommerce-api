package com.build.ecommerce.core.security.login.user;

import com.build.ecommerce.core.security.exception.AuthenticationFailException;
import com.build.ecommerce.core.security.login.common.dto.request.LoginRequest;
import com.build.ecommerce.core.security.login.common.token.impl.CustomUserLoginToken;
import com.build.ecommerce.core.security.login.util.FilterUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Validator;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

import java.io.IOException;

public class CustomUserLoginFilter extends AbstractAuthenticationProcessingFilter {

    /* Filter Setting */
    private final static String LOGIN_REQUEST_URL = "/v1/login/user";


    private final Validator validator;

    public CustomUserLoginFilter(Validator validator) {
        super(FilterUtil.getRequestMatcher(LOGIN_REQUEST_URL));
        this.validator = validator;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (!FilterUtil.isApplicationJson(request.getContentType())) {
            throw new AuthenticationFailException("지원하지 않는 Content-Type 입니다.");
        }

        LoginRequest loginRequest = LoginRequest.parseDto(request, validator);

        return getAuthenticationManager().authenticate(
                CustomUserLoginToken.toUnAuthenticate(
                        loginRequest.email(),
                        loginRequest.password()
                )
        );
    }
}
