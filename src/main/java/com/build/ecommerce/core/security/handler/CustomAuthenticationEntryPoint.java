
package com.build.ecommerce.core.security.handler;

import com.build.ecommerce.core.exception.ExceptionCode;
import com.build.ecommerce.core.support.servlet.CustomHandlerUtil;
import com.build.ecommerce.common.dto.FailResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        ExceptionCode exceptionCode = CustomHandlerUtil.defineException(exception);
        CustomHandlerUtil.toResponse(response, exceptionCode.getHttpStatus());
        new ObjectMapper().writeValue(response.getWriter(), FailResponse.toResponse(exceptionCode));
    }
}
