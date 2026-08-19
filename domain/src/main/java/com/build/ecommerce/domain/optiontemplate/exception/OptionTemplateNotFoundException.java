package com.build.ecommerce.domain.optiontemplate.exception;

import com.build.ecommerce.core.exception.ApplicationException;
import com.build.ecommerce.core.exception.code.ExceptionCode;

import static com.build.ecommerce.core.exception.code.ExceptionCode.NOT_FOUND;

public class OptionTemplateNotFoundException extends ApplicationException {

    private static final ExceptionCode EXCEPTION_CODE = NOT_FOUND;
    private static final String DEFAULT_MESSAGE = "옵션 템플릿 정보를 찾을 수 없습니다.";

    public OptionTemplateNotFoundException() {
        super(EXCEPTION_CODE, DEFAULT_MESSAGE);
    }

    public OptionTemplateNotFoundException(String message) {
        super(EXCEPTION_CODE, message);
    }
}
