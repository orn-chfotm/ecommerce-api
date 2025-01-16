package com.build.ecommerce.domain.product.exception;

import com.build.ecommerce.core.exception.ApplicationException;
import com.build.ecommerce.core.exception.code.ExceptionCode;

import static com.build.ecommerce.core.exception.code.ExceptionCode.PRODUCT_NOT_FOUND;

public class ProductNotFountException extends ApplicationException {

    private static final ExceptionCode EXCEPTION_CODE = PRODUCT_NOT_FOUND;

    public ProductNotFountException() {
        super(EXCEPTION_CODE);
    }

    public ProductNotFountException(String message) {
        super(EXCEPTION_CODE, message);
    }
}
