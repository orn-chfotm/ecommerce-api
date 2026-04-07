package com.build.ecommerce.domain.order.exception;

import com.build.ecommerce.core.exception.ApplicationException;
import com.build.ecommerce.core.exception.ExceptionCode;

public class OrderStatusException extends ApplicationException {

    private static final ExceptionCode EXCEPTION_CODE = ExceptionCode.ORDER_NOT_CANCEL;

    public OrderStatusException() {
        super(EXCEPTION_CODE);
    }

    public OrderStatusException(String message) {
        super(EXCEPTION_CODE, message);
    }
}
