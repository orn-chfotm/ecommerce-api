package com.build.ecommerce.domain.product.exception;

import com.build.ecommerce.core.error.ApplicationException;
import com.build.ecommerce.core.error.ExceptionCode;

import static com.build.ecommerce.core.error.ExceptionCode.PRODUCT_NOT_ENOUGH_STOCK;

public class ProductNotEnoughStockException extends ApplicationException {

    private static final ExceptionCode EXCEPTION_CODE = PRODUCT_NOT_ENOUGH_STOCK;

    public ProductNotEnoughStockException() {
        super(EXCEPTION_CODE);
    }

    public ProductNotEnoughStockException(String message) {
        super(EXCEPTION_CODE, message);
    }
}
