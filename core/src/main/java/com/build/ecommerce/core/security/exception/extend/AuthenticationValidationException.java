package com.build.ecommerce.core.security.exception.extend;


import com.build.ecommerce.core.response.ValidationErrorResponse;
import com.build.ecommerce.core.security.exception.SecurityAuthenticationException;

import java.util.Collections;
import java.util.List;

import static com.build.ecommerce.core.exception.code.ExceptionCode.AUTHENTICATION_VALID_FAIL;

public class AuthenticationValidationException extends SecurityAuthenticationException {

    private final List<ValidationErrorResponse> errorList;

    public AuthenticationValidationException() {
        super(AUTHENTICATION_VALID_FAIL);
        this.errorList = Collections.emptyList();
    }

    public AuthenticationValidationException(List<ValidationErrorResponse> errorList) {
        super(AUTHENTICATION_VALID_FAIL);
        this.errorList = errorList;
    }

    public List<ValidationErrorResponse> getErrorList() {
        return this.errorList;
    }
}
