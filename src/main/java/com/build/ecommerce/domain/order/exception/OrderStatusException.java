package com.build.ecommerce.domain.order.exception;

import com.build.ecommerce.core.error.ApplicationException;
import com.build.ecommerce.core.error.ExceptionCode;

public class OrderStatusException extends ApplicationException {

    private static final ExceptionCode EXCEPTION_CODE = ExceptionCode.ORDER_NOT_CANCLE;

    public OrderStatusException() {
        super(EXCEPTION_CODE);
    }

    public OrderStatusException(String message) {
        super(EXCEPTION_CODE, message);
    }
}
