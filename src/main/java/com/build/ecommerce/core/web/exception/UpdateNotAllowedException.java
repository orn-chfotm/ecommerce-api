package com.build.ecommerce.core.web.exception;

import com.build.ecommerce.core.exception.ApplicationException;
import com.build.ecommerce.core.exception.ExceptionCode;

import static com.build.ecommerce.core.exception.ExceptionCode.NOT_FOUND;

public class UpdateNotAllowedException extends ApplicationException {
    private static final ExceptionCode EXCEPTION_CODE = NOT_FOUND;
    public static final String DEFAULT_MESSAGE = "삭제 처리 시 오류가 발생했습니다.";

    public UpdateNotAllowedException() {
        super(EXCEPTION_CODE, DEFAULT_MESSAGE);
    }

    public UpdateNotAllowedException(ExceptionCode exceptionCode) {
        super(exceptionCode);
    }

    public UpdateNotAllowedException(String message) {
        super(EXCEPTION_CODE, message);
    }

    public UpdateNotAllowedException(ExceptionCode exceptionCode, String message) {
        super(exceptionCode, message);
    }
}
