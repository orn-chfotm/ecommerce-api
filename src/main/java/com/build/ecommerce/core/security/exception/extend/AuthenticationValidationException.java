package com.build.ecommerce.core.security.exception;


import com.build.ecommerce.core.dto.response.ValidationErrorResponse;
import com.build.ecommerce.core.error.ExceptionCode;
import com.build.ecommerce.core.security.error.SecurityAuthenticationException;

import java.util.Collections;
import java.util.List;

public class AuthenticationValidationException extends SecurityAuthenticationException {

    private static final ExceptionCode AUTHENTICATION_FAIL = ExceptionCode.AUTHENTICATION_VALID_FAIL;
    private final List<ValidationErrorResponse> errorList;

    public AuthenticationValidationException() {
        super(AUTHENTICATION_FAIL.getMessage(), AUTHENTICATION_FAIL);
        this.errorList = Collections.emptyList();
    }

    public AuthenticationValidationException(List<ValidationErrorResponse> errorList) {
        super(AUTHENTICATION_FAIL.getMessage(), AUTHENTICATION_FAIL);
        this.errorList = errorList;
    }

    public List<ValidationErrorResponse> getErrorList() {
        return this.errorList;
    }
}
