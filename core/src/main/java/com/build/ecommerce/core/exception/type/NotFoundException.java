package com.build.ecommerce.core.exception.type;

import com.build.ecommerce.core.exception.ApplicationException;
import com.build.ecommerce.core.exception.ErrorCode;
import com.build.ecommerce.core.exception.code.ExceptionCode;

import static com.build.ecommerce.core.exception.code.ExceptionCode.NOT_FOUND;

public class NotFoundException extends ApplicationException {
    private static final ExceptionCode EXCEPTION_CODE = NOT_FOUND;

    public NotFoundException() {
        super(EXCEPTION_CODE);
    }

    public NotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}
