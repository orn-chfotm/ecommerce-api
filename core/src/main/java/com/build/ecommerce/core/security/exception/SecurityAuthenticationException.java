package com.build.ecommerce.core.security.exception;

import com.build.ecommerce.core.exception.ErrorCode;
import lombok.Getter;
import org.springframework.security.core.AuthenticationException;

@Getter
public abstract class SecurityAuthenticationException extends AuthenticationException {

    private final ErrorCode exceptionCode;

    protected SecurityAuthenticationException(String message, ErrorCode exceptionCode) {
        super(message);
        this.exceptionCode = exceptionCode;
    }

    protected SecurityAuthenticationException(String message, ErrorCode exceptionCode, Throwable cause) {
        super(message, cause);
        this.exceptionCode = exceptionCode;
    }
}
