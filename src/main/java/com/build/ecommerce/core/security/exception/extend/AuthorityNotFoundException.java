package com.build.ecommerce.core.security.exception;


import com.build.ecommerce.core.error.ExceptionCode;
import com.build.ecommerce.core.security.error.SecurityAuthenticationException;

public class AuthorityNotFoundException extends SecurityAuthenticationException {

    private static final ExceptionCode AUTHENTICATION_FAIL = ExceptionCode.AUTHENTICATION_UNAUTHORIZED;

    public AuthorityNotFoundException() {
        super(AUTHENTICATION_FAIL.getMessage(), AUTHENTICATION_FAIL);
    }

    public AuthorityNotFoundException(String message) {
        super(message, AUTHENTICATION_FAIL);
    }
}
