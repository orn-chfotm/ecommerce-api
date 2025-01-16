package com.build.ecommerce.core.security.handler;

import com.build.ecommerce.core.dto.response.FailResponse;
import com.build.ecommerce.core.exception.code.ExceptionCode;
import com.build.ecommerce.core.security.util.HandlerUtil;
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
        HandlerUtil.toResponse(response, EXCEPTION_CODE.getHttpStatus());
        new ObjectMapper().writeValue(response.getWriter(), FailResponse.toResponse(EXCEPTION_CODE));
    }
}
