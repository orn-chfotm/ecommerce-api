package com.build.ecommerce.core.security.exception.extend;



import com.build.ecommerce.core.exception.ExceptionCode;
import com.build.ecommerce.core.security.exception.SecurityAuthenticationException;
import com.build.ecommerce.core.web.dto.ValidationErrorResponse;

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
