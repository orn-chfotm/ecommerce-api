package com.build.ecommerce.core.security.login.common.handler;

import com.build.ecommerce.core.dto.response.FailResponse;
import com.build.ecommerce.core.dto.response.ValidationErrorResponse;
import com.build.ecommerce.core.error.ExceptionCode;
import com.build.ecommerce.core.security.exception.AuthenticationValidationException;
import com.build.ecommerce.core.util.CustomHandlerUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.validation.BindException;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

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
