package com.build.ecommerce.domain.admin.exception;


import com.build.ecommerce.core.exception.ApplicationException;
import com.build.ecommerce.core.exception.ExceptionCode;

public class AdminNotFoundException extends ApplicationException {

    private static final ExceptionCode EXCEPTION_CODE = ExceptionCode.NOT_FOUND;
    private static final String DEFAULT_MESSAGE = "관리자 정보를 찾을 수 없습니다.";

    public AdminNotFoundException() {
        super(EXCEPTION_CODE, DEFAULT_MESSAGE);
    }

    public AdminNotFoundException(String message) {
        super(EXCEPTION_CODE, message);
    }
}
