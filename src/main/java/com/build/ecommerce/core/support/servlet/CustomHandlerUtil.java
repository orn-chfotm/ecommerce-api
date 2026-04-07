package com.build.ecommerce.core.support.servlet;

import com.build.ecommerce.core.exception.ExceptionCode;
import com.build.ecommerce.core.security.exception.SecurityAuthenticationException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.nio.charset.StandardCharsets;

public class CustomHandlerUtil {

    public static void toResponse(HttpServletResponse response, HttpStatus status) {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
    }

    public static ExceptionCode defineException(RuntimeException exception) {
        ExceptionCode exceptionCode = ExceptionCode.AUTHENTICATION_UNAUTHORIZED;
        if (exception instanceof SecurityAuthenticationException securityAuthException) {
            exceptionCode = securityAuthException.getExceptionCode();
        }
        return exceptionCode;
    }
}
