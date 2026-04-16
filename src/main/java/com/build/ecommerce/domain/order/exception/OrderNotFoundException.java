package com.build.ecommerce.domain.order.exception;

import com.build.ecommerce.core.exception.ApplicationException;
import com.build.ecommerce.core.exception.ExceptionCode;

public class OrderNotFoundException extends ApplicationException {

    private static final ExceptionCode EXCEPTION_CODE = ExceptionCode.NOT_FOUND;
    private static final String DEFAULT_MESSAGE = "주문 정보를 찾을 수 없습니다.";

    public OrderNotFoundException() {
        super(EXCEPTION_CODE, DEFAULT_MESSAGE);
    }

    public OrderNotFoundException(String message) {
        super(EXCEPTION_CODE, message);
    }
}
