package com.build.ecommerce.domain.admin.exception;

import com.build.ecommerce.core.error.ApplicationException;
import com.build.ecommerce.core.error.ExceptionCode;

public class AdminExistException extends ApplicationException {

    private static final ExceptionCode EXCEPTION_CODE = ExceptionCode.ADMIN_EXIST;

    public AdminExistException() {
        super(EXCEPTION_CODE);
    }

    public AdminExistException(String message) {
        super(EXCEPTION_CODE, message);
    }
}
