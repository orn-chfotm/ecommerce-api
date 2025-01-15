package com.build.ecommerce.core.jwt.exception;

import com.build.ecommerce.core.error.ExceptionCode;
import com.build.ecommerce.core.jwt.error.SecurityAuthenticationException;

public class AuthenticationFailException extends SecurityAuthenticationException {

    private static final ExceptionCode AUTHENTICATION_FAIL = ExceptionCode.AUTHENTICATION_UNAUTHORIZED;

    public AuthenticationFailException() {
        super(AUTHENTICATION_FAIL.getMessage(), AUTHENTICATION_FAIL);
    }

    public AuthenticationFailException(String message) {
        super(message, AUTHENTICATION_FAIL);
    }
}
