package com.build.ecommerce.domain.order.exception;

import com.build.ecommerce.core.exception.ApplicationException;
import com.build.ecommerce.core.exception.ExceptionCode;

public class OrderStatusException extends ApplicationException {

    private static final ExceptionCode EXCEPTION_CODE = ExceptionCode.CONFLICT;
    public static final String DEFAULT_MESSAGE = "취소 불가능 상태입니다.";

    public OrderStatusException() {
        super(EXCEPTION_CODE, DEFAULT_MESSAGE);
    }

    public OrderStatusException(String message) {
        super(EXCEPTION_CODE, message);
    }
}
