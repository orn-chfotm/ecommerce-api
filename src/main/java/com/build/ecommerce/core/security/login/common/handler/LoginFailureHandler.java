package com.build.ecommerce.core.security.login.common.handler;

import com.build.ecommerce.core.exception.ExceptionCode;
import com.build.ecommerce.core.security.exception.extend.AuthenticationValidationException;
import com.build.ecommerce.core.support.servlet.CustomHandlerUtil;
import com.build.ecommerce.common.dto.FailResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import java.io.IOException;

public class LoginFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        ExceptionCode exceptionCode = CustomHandlerUtil.defineException(exception);
        CustomHandlerUtil.toResponse(response, exceptionCode.getHttpStatus());

        FailResponse<?> responseEntity;
        if (exception instanceof AuthenticationValidationException validationException) {
            responseEntity = FailResponse.toResponse(exceptionCode, validationException.getErrorList()).getBody();
        } else {
            responseEntity = FailResponse.toResponse(exceptionCode).getBody();
        }

        new ObjectMapper().writeValue(response.getWriter(), responseEntity);
    }
}
