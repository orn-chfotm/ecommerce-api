
package com.build.ecommerce.core.security.handler;

import com.build.ecommerce.core.exception.ErrorCode;
import com.build.ecommerce.core.security.exception.SecurityAuthenticationException;
import com.build.ecommerce.core.support.servlet.CustomHandlerUtil;
import com.build.ecommerce.core.response.FailResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        ErrorCode exceptionCode = CustomHandlerUtil.defineException(exception);
        CustomHandlerUtil.toResponse(response, exceptionCode.getHttpStatus());

        FailResponse<?> responseEntity;
        if (exception instanceof SecurityAuthenticationException securityAuthenticationException) {
            responseEntity = FailResponse.toResponse(securityAuthenticationException).getBody();
        } else {
            responseEntity = FailResponse.toResponse(exceptionCode).getBody();
        }

        objectMapper.writeValue(response.getWriter(), responseEntity);
    }
}
