package com.build.ecommerce.core.security.exception;

import com.build.ecommerce.core.exception.ErrorCode;
import lombok.Getter;
import org.springframework.security.core.AuthenticationException;

@Getter
public abstract class SecurityAuthenticationException extends AuthenticationException {

    private final ErrorCode exceptionCode;

    protected SecurityAuthenticationException(ErrorCode exceptionCode) {
        super(exceptionCode.getMessage());
        this.exceptionCode = exceptionCode;
    }

    protected SecurityAuthenticationException(ErrorCode exceptionCode, String message) {
        super(message);
        this.exceptionCode = exceptionCode;
    }

    protected SecurityAuthenticationException(ErrorCode exceptionCode, Throwable cause) {
        super(exceptionCode.getMessage(), cause);
        this.exceptionCode = exceptionCode;
    }

    protected SecurityAuthenticationException(ErrorCode exceptionCode, String message, Throwable cause) {
        super(message, cause);
        this.exceptionCode = exceptionCode;
    }
}
