package com.build.ecommerce.domain.product.exception;

import com.build.ecommerce.core.exception.ApplicationException;
import com.build.ecommerce.core.exception.ExceptionCode;

import static com.build.ecommerce.core.exception.ExceptionCode.CONFLICT;

public class ProductNotEnoughStockException extends ApplicationException {

    private static final ExceptionCode EXCEPTION_CODE = CONFLICT;
    public static final String DEFAULT_MESSAGE = "주문 상품의 재고가 부족합니다.";

    public ProductNotEnoughStockException() {
        super(EXCEPTION_CODE, DEFAULT_MESSAGE);
    }

    public ProductNotEnoughStockException(String message) {
        super(EXCEPTION_CODE, message);
    }
}
