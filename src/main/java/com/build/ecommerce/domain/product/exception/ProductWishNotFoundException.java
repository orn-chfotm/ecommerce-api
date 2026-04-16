package com.build.ecommerce.domain.product.exception;

import com.build.ecommerce.core.exception.ApplicationException;
import com.build.ecommerce.core.exception.ExceptionCode;

import static com.build.ecommerce.core.exception.ExceptionCode.NOT_FOUND;

public class ProductWishNotFoundException extends ApplicationException {

    private static final ExceptionCode EXCEPTION_CODE = NOT_FOUND;
    private static final String DEFAULT_MESSAGE = "찜하기 제품 정보를 찾을 수 없습니다.";

    public ProductWishNotFoundException() {
        super(EXCEPTION_CODE, DEFAULT_MESSAGE);
    }

    public ProductWishNotFoundException(String message) {
        super(EXCEPTION_CODE, message);
    }
}
