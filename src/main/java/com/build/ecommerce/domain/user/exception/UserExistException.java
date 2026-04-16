package com.build.ecommerce.domain.user.exception;

import com.build.ecommerce.core.exception.ApplicationException;
import com.build.ecommerce.core.exception.ExceptionCode;

import static com.build.ecommerce.core.exception.ExceptionCode.CONFLICT;

public class UserExistException extends ApplicationException {

    private static final ExceptionCode EXCEPTION_CODE = CONFLICT;
    public static final String DEFAULT_MESSAGE = "이미 존재하는 사용자입니다.";

    public UserExistException() {
        super(EXCEPTION_CODE, DEFAULT_MESSAGE);
    }

    public UserExistException(String message) {
        super(EXCEPTION_CODE, message);
    }
}
