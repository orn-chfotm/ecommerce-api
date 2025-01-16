package com.build.ecommerce.domain.order.exception;

import com.build.ecommerce.core.exception.ApplicationException;
import com.build.ecommerce.core.exception.code.ExceptionCode;

public class OrderNotFountException extends ApplicationException {

    private static final ExceptionCode EXCEPTION_CODE = ExceptionCode.ORDER_NOT_FOUND;

    public OrderNotFountException() {
        super(EXCEPTION_CODE);
    }

    public OrderNotFountException(String message) {
        super(EXCEPTION_CODE, message);
    }
}
