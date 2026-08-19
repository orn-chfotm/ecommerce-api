package com.build.ecommerce.domain.cart.exception;

import com.build.ecommerce.core.exception.ApplicationException;
import com.build.ecommerce.core.exception.code.ExceptionCode;

public class CartNotFoundException extends ApplicationException {
    public CartNotFoundException() {
        super(ExceptionCode.NOT_FOUND);
    }
}
