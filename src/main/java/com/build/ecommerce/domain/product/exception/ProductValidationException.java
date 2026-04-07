package com.build.ecommerce.domain.product.exception;

import com.build.ecommerce.core.exception.ApplicationException;
import com.build.ecommerce.core.exception.ExceptionCode;

import static com.build.ecommerce.core.exception.ExceptionCode.BAD_REQUEST;

public class ProductValidationException extends ApplicationException {

    private static final ExceptionCode EXCEPTION_CODE = BAD_REQUEST;

    public ProductValidationException() {
        super(EXCEPTION_CODE);
    }

    public ProductValidationException(String message) {
        super(EXCEPTION_CODE, message);
    }
}
