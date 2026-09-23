package com.build.ecommerce.core.security.exception.extend;


import com.build.ecommerce.core.exception.ErrorCode;
import com.build.ecommerce.core.security.exception.SecurityAuthenticationException;

import static com.build.ecommerce.core.exception.code.ExceptionCode.AUTHORITY_NOT_FOUND;

public class AuthenticationFailException extends SecurityAuthenticationException {

    public AuthenticationFailException() {
        this(AUTHORITY_NOT_FOUND);
    }

    public AuthenticationFailException(ErrorCode errorCode) {
        super(errorCode);
    }

    public AuthenticationFailException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public AuthenticationFailException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
}
