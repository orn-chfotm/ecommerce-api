package com.build.ecommerce.domain.product.exception;

import com.build.ecommerce.core.exception.ApplicationException;
import com.build.ecommerce.core.exception.code.ExceptionCode;

import static com.build.ecommerce.core.exception.code.ExceptionCode.CONFLICT;

public class ProductOptionAlreadyRegisteredException extends ApplicationException {

    private static final ExceptionCode EXCEPTION_CODE = CONFLICT;
    private static final String DEFAULT_MESSAGE = "이미 옵션이 등록된 상품입니다.";

    public ProductOptionAlreadyRegisteredException() {
        super(EXCEPTION_CODE, DEFAULT_MESSAGE);
    }

    public ProductOptionAlreadyRegisteredException(String message) {
        super(EXCEPTION_CODE, message);
    }
}
