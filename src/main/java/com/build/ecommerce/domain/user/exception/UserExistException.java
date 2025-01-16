package com.build.ecommerce.domain.user.exception;

import com.build.ecommerce.core.exception.ApplicationException;
import com.build.ecommerce.core.exception.code.ExceptionCode;

import static com.build.ecommerce.core.exception.code.ExceptionCode.USER_EXIST;

public class UserExistException extends ApplicationException {

    private static final ExceptionCode EXCEPTION_CODE = USER_EXIST;

    public UserExistException() {
        super(EXCEPTION_CODE);
    }

    public UserExistException(String message) {
        super(EXCEPTION_CODE, message);
    }
}
