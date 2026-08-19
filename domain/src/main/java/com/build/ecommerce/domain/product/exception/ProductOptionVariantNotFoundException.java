package com.build.ecommerce.domain.product.exception;

import com.build.ecommerce.core.exception.ApplicationException;
import com.build.ecommerce.core.exception.code.ExceptionCode;

import static com.build.ecommerce.core.exception.code.ExceptionCode.NOT_FOUND;

public class ProductOptionVariantNotFoundException extends ApplicationException {

    private static final ExceptionCode EXCEPTION_CODE = NOT_FOUND;
    private static final String DEFAULT_MESSAGE = "상품 옵션 조합 정보를 찾을 수 없습니다.";

    public ProductOptionVariantNotFoundException() {
        super(EXCEPTION_CODE, DEFAULT_MESSAGE);
    }

    public ProductOptionVariantNotFoundException(String message) {
        super(EXCEPTION_CODE, message);
    }
}
