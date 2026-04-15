package com.build.ecommerce.core.security.exception.extend;


import com.build.ecommerce.core.exception.ExceptionCode;
import com.build.ecommerce.core.security.exception.SecurityAuthenticationException;

public class AuthorityNotFoundException extends SecurityAuthenticationException {

    private static final ExceptionCode AUTHENTICATION_FAIL = ExceptionCode.AUTHENTICATION_UNAUTHORIZED;

    public AuthorityNotFoundException() {
        super(AUTHENTICATION_FAIL.getMessage(), AUTHENTICATION_FAIL);
    }

    public AuthorityNotFoundException(String message) {
        super(message, AUTHENTICATION_FAIL);
    }
}
