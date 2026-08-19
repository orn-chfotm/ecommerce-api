package com.build.ecommerce.domain.address.exception.code;

import com.build.ecommerce.core.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ExceptionCode implements ErrorCode {

    ADDRESS_NOT_FOUND(HttpStatus.NOT_FOUND, "주소 정보를 찾을 수 없습니다.")
    ;

    private final HttpStatus httpStatus;
    private final String message;
}
