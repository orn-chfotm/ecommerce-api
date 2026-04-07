package com.build.ecommerce.core.security.exception;

import com.build.ecommerce.core.exception.ExceptionCode;
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
