package com.build.ecommerce.core.exception.type;

import com.build.ecommerce.core.exception.ApplicationException;
import com.build.ecommerce.core.exception.code.ExceptionCode;

import static com.build.ecommerce.core.exception.code.ExceptionCode.VALIDATION_EXCEPTION;

public class InvalidInputException extends ApplicationException {
    private static final ExceptionCode EXCEPTION_CODE = VALIDATION_EXCEPTION;

    public InvalidInputException() {
        super(EXCEPTION_CODE);
    }

    public InvalidInputException(ExceptionCode exceptionCode) {
        super(exceptionCode);
    }

    public InvalidInputException(String message) {
        super(EXCEPTION_CODE, message);
    }

    public InvalidInputException(ExceptionCode exceptionCode, String message) {
        super(exceptionCode, message);
    }
}
