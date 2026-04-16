package com.build.ecommerce.domain.admin.exception;

import com.build.ecommerce.core.exception.ApplicationException;
import com.build.ecommerce.core.exception.ExceptionCode;

public class AdminExistException extends ApplicationException {

    private static final ExceptionCode EXCEPTION_CODE = ExceptionCode.CONFLICT;
    private static final String DEFAULT_MESSAGE = "이미 존재하는 관리자입니다.";

    public AdminExistException() {
        super(EXCEPTION_CODE, DEFAULT_MESSAGE);
    }

    public AdminExistException(String message) {
        super(EXCEPTION_CODE, message);
    }
}
