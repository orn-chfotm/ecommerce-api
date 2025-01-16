package com.build.ecommerce.core.exception;

import com.build.ecommerce.core.exception.code.ExceptionCode;
import lombok.Getter;
import org.springframework.security.core.AuthenticationException;

@Getter
public abstract class SecurityAuthenticationException extends AuthenticationException {

    private final ExceptionCode exceptionCode;

    protected SecurityAuthenticationException(String message, ExceptionCode exceptionCode) {
        super(message);
        this.exceptionCode = exceptionCode;
    }
}
