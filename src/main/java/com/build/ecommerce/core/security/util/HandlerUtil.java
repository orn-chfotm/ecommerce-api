package com.build.ecommerce.core.security.util;

import com.build.ecommerce.core.exception.code.ExceptionCode;
import com.build.ecommerce.core.exception.SecurityAuthenticationException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.nio.charset.StandardCharsets;

public class HandlerUtil {

    public static void toResponse(HttpServletResponse response) {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
    }

    public static void toResponse(HttpServletResponse response, HttpStatus status) {
        toResponse(response);
        response.setStatus(status.value());
    }

    public static ExceptionCode defineException(RuntimeException exception) {
        ExceptionCode exceptionCode = ExceptionCode.AUTHENTICATION_UNAUTHORIZED;
        if (exception instanceof SecurityAuthenticationException securityAuthException) {
            exceptionCode = securityAuthException.getExceptionCode();
        }
        return exceptionCode;
    }
}
