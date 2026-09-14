package com.build.ecommerce.domain.optiontemplate.exception.code;

import com.build.ecommerce.core.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum OptionTemplateExceptionCode implements ErrorCode {

    OPTION_TEMPLATE_NOT_FOUND(HttpStatus.NOT_FOUND, "옵션 템플릿 정보를 찾을 수 없습니다.")
    ;

    private final HttpStatus httpStatus;
    private final String message;
}
