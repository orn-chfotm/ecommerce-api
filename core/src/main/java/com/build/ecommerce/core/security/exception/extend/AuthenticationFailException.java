package com.build.ecommerce.core.security.exception.extend;


import com.build.ecommerce.core.exception.ErrorCode;
import com.build.ecommerce.core.exception.code.ExceptionCode;
import com.build.ecommerce.core.security.exception.SecurityAuthenticationException;

public class AuthenticationFailException extends SecurityAuthenticationException {

    public AuthenticationFailException() {
        this(ExceptionCode.AUTHORITY_NOT_FOUND);
    }

    public AuthenticationFailException(ErrorCode errorCode) {
        super(errorCode.getMessage(), errorCode);
    }

    public AuthenticationFailException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), errorCode, cause);
    }
}
