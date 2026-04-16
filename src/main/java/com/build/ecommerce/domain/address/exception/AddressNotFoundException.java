package com.build.ecommerce.domain.address.exception;

import com.build.ecommerce.core.exception.ApplicationException;
import com.build.ecommerce.core.exception.ExceptionCode;

public class AddressNotFoundException extends ApplicationException {

    private static final ExceptionCode EXCEPTION_CODE = ExceptionCode.NOT_FOUND;
    public static final String DEFAULT_MESSAGE = "주소 정보를 찾을 수 없습니다.";

    public AddressNotFoundException() {
        super(EXCEPTION_CODE, DEFAULT_MESSAGE);
    }

    public AddressNotFoundException(String message) {
        super(EXCEPTION_CODE, message);
    }
}
