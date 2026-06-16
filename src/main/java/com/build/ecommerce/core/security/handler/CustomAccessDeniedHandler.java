package com.build.ecommerce.core.security.handler;

import com.build.ecommerce.core.exception.code.ExceptionCode;
import com.build.ecommerce.core.support.servlet.CustomHandlerUtil;
import com.build.ecommerce.core.response.FailResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;

@RequiredArgsConstructor
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;
    private static final ExceptionCode EXCEPTION_CODE = ExceptionCode.AUTHENTICATION_FORBIDDEN;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException exception) throws IOException, ServletException {
        CustomHandlerUtil.toResponse(response, EXCEPTION_CODE.getHttpStatus());
        objectMapper.writeValue(response.getWriter(), FailResponse.toResponse(EXCEPTION_CODE).getBody());
    }
}
