package com.build.ecommerce.common.exception;

import com.build.ecommerce.core.exception.ApplicationException;
import com.build.ecommerce.core.exception.ExceptionCode;

import static com.build.ecommerce.core.exception.ExceptionCode.NOT_FOUND;

public class DeleteNotAllowedException extends ApplicationException {
    private static final ExceptionCode EXCEPTION_CODE = NOT_FOUND;
    public static final String DEFAULT_MESSAGE = "삭제 처리 시 오류가 발생했습니다.";

    public DeleteNotAllowedException() {
        super(EXCEPTION_CODE, DEFAULT_MESSAGE);
    }

    public DeleteNotAllowedException(ExceptionCode exceptionCode) {
        super(exceptionCode);
    }

    public DeleteNotAllowedException(String message) {
        super(EXCEPTION_CODE, message);
    }

    public DeleteNotAllowedException(ExceptionCode exceptionCode, String message) {
        super(exceptionCode, message);
    }
}
