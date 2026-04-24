package com.build.ecommerce.core.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ExceptionCode {

    // Exception Default - Domain
    EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR, "잠시후 다시 시도해주세요."),
    VALIDATION_EXCEPTION(HttpStatus.BAD_REQUEST, "요청 값을 확인해주세요."),
    NOT_FOUND(HttpStatus.NOT_FOUND, "정보를 찾을 수 없습니다."),
    CONFLICT(HttpStatus.CONFLICT, "요청을 처리할 수 없는 상태입니다."),

    // Security Exception
    AUTHENTICATION_VALID_FAIL(HttpStatus.BAD_REQUEST, "로그인 정보를 확인해주세요."),
    AUTHENTICATION_UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "로그인 정보를 찾을 수 없습니다."),
    AUTHENTICATION_FORBIDDEN(HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),
    AUTHORITY_NOT_FOUND(HttpStatus.CONFLICT, "권한이 불명확합니다.");

    private final HttpStatus httpStatus;
    private final String message;

    ExceptionCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
