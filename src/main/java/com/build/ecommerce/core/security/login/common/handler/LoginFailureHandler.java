package com.build.ecommerce.core.security.login.common.handler;

import com.build.ecommerce.core.exception.ExceptionCode;
import com.build.ecommerce.core.security.exception.extend.AuthenticationValidationException;
import com.build.ecommerce.core.support.servlet.CustomHandlerUtil;
import com.build.ecommerce.core.web.dto.FailResponse;
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
        ExceptionCode exceptionCode = CustomHandlerUtil.defineException(exception);
        CustomHandlerUtil.toResponse(response, exceptionCode.getHttpStatus());

        Object responseBody;
        if (exception instanceof AuthenticationValidationException validationException) {
            responseBody = FailResponse.toResponse(exceptionCode, validationException.getErrorList());
        } else {
            responseBody = FailResponse.toResponse(exceptionCode, exception.getMessage());
        }

        new ObjectMapper().writeValue(response.getWriter(), responseBody);
    }
}
