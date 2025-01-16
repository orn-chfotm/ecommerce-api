package com.build.ecommerce.core.security.handler.login;

import com.build.ecommerce.core.dto.response.FailResponse;
import com.build.ecommerce.core.exception.code.ExceptionCode;
import com.build.ecommerce.core.security.util.HandlerUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import java.io.IOException;

public class LoginFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        ExceptionCode exceptionCode = HandlerUtil.defineException(exception);
        HandlerUtil.toResponse(response, exceptionCode.getHttpStatus());
        new ObjectMapper().writeValue(response.getWriter(), FailResponse.toResponse(exceptionCode));
    }
}
