package com.build.ecommerce.core.exception;

import com.build.ecommerce.core.exception.code.ExceptionCode;
import lombok.Getter;

@Getter
public abstract class ApplicationException extends RuntimeException {

    private final ExceptionCode exceptionCode;

    protected ApplicationException(ExceptionCode exceptionCode) {
        super(exceptionCode.getMessage());
        this.exceptionCode = exceptionCode;
    }

    protected ApplicationException(ExceptionCode exceptionCode, String message) {
        super(message);
        this.exceptionCode = exceptionCode;
    }
}
