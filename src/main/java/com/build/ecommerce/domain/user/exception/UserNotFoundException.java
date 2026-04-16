package com.build.ecommerce.domain.user.exception;

import com.build.ecommerce.core.exception.ApplicationException;
import com.build.ecommerce.core.exception.ExceptionCode;

import static com.build.ecommerce.core.exception.ExceptionCode.NOT_FOUND;

public class UserNotFoundException extends ApplicationException {

    private static final ExceptionCode EXCEPTION_CODE = NOT_FOUND;
    public static final String DEFAULT_MESSAGE = "사용자 정보를 찾을 수 없습니다.";

    public UserNotFoundException() {
        super(EXCEPTION_CODE, DEFAULT_MESSAGE);
    }

    public UserNotFoundException(String message) {
        super(EXCEPTION_CODE, message);
    }
}
