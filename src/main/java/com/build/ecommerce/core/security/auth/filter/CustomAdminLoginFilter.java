package com.build.ecommerce.core.security.auth.filter;

import com.build.ecommerce.core.security.exception.AuthenticationFailException;
import com.build.ecommerce.core.security.dto.request.LoginRequest;
import com.build.ecommerce.core.security.auth.token.login.impl.CustomAdminLoginToken;
import com.build.ecommerce.core.security.util.FilterUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Validator;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

import java.io.IOException;

public class CustomAdminLoginFilter extends AbstractAuthenticationProcessingFilter {


    private final static String LOGIN_REQUEST_URL = "/v1/login/admin";
    private final Validator validator;

    public CustomAdminLoginFilter(Validator validator) {
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
                CustomAdminLoginToken.toUnAuthenticate(
                        loginRequest.email(),
                        loginRequest.password()
                )
        );
    }
}
