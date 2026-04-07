package com.build.ecommerce.core.security.handler;

import com.build.ecommerce.core.exception.ExceptionCode;
import com.build.ecommerce.core.support.servlet.CustomHandlerUtil;
import com.build.ecommerce.core.web.dto.FailResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;

public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private static final ExceptionCode EXCEPTION_CODE = ExceptionCode.AUTHENTICATION_FORBIDDEN;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException exception) throws IOException, ServletException {
        CustomHandlerUtil.toResponse(response, EXCEPTION_CODE.getHttpStatus());
        new ObjectMapper().writeValue(response.getWriter(), FailResponse.toResponse(EXCEPTION_CODE));
    }
}
