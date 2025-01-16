package com.build.ecommerce.domain.admin.exception;


import com.build.ecommerce.core.exception.ApplicationException;
import com.build.ecommerce.core.exception.code.ExceptionCode;

public class AdminNotFountException extends ApplicationException {

    private static final ExceptionCode EXCEPTION_CODE = ExceptionCode.ADMIN_NOT_FOUND;

    public AdminNotFountException() {
        super(EXCEPTION_CODE);
    }

    public AdminNotFountException(String message) {
        super(EXCEPTION_CODE, message);
    }
}
