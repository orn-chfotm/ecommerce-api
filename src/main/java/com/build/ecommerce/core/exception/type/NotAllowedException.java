package com.build.ecommerce.core.exception.type;

import com.build.ecommerce.core.exception.ApplicationException;
import com.build.ecommerce.core.exception.code.ExceptionCode;

import static com.build.ecommerce.core.exception.code.ExceptionCode.CONFLICT;

public class NotAllowedException extends ApplicationException {
    private static final ExceptionCode EXCEPTION_CODE = CONFLICT;

    public NotAllowedException() {
        super(EXCEPTION_CODE, EXCEPTION_CODE.getMessage());
    }

    public NotAllowedException(ExceptionCode exceptionCode) {
        super(exceptionCode);
    }

    public NotAllowedException(String message) {
        super(EXCEPTION_CODE, message);
    }

    public NotAllowedException(ExceptionCode exceptionCode, String message) {
        super(exceptionCode, message);
    }
}
