package com.build.ecommerce.core.exception.type;

import com.build.ecommerce.core.exception.ApplicationException;
import com.build.ecommerce.core.exception.code.ExceptionCode;

import static com.build.ecommerce.core.exception.code.ExceptionCode.CONFLICT;

public class BusinessException extends ApplicationException {
    private static final ExceptionCode EXCEPTION_CODE = CONFLICT;

    public BusinessException() {
        super(EXCEPTION_CODE);
    }

    public BusinessException(ExceptionCode exceptionCode) {
        super(exceptionCode);
    }
}
