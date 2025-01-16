package com.build.ecommerce.core.exception.code;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ExceptionCode {

    // Exception Defualt
    EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR, "잠시후 다시 시도해주세요."),
    VALIDATION_EXCEPTION(HttpStatus.BAD_REQUEST, "요청 값을 확인해주세요."),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "요청 정보를 확인해주세요."),
    NOT_FOUND(HttpStatus.NOT_FOUND, "정보를 찾을 수 없습니다."),

    // Security Exception
    AUTHENTICATION_UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증 정보를 찾을 수 없습니다."),
    AUTHENTICATION_FORBIDDEN(HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),
    AUTHORITY_NOT_FOUND(HttpStatus.CONFLICT, "권한이 불명확합니다."),

    // 사용자
    USER_EXIST(HttpStatus.BAD_REQUEST, "이미 존재하는 사용자입니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자 정보를 찾을 수 없습니다."),

    // 관리자
    ADMIN_EXIST(HttpStatus.BAD_REQUEST, "이미 존재하는 사용자입니다."),
    ADMIN_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자 정보를 찾을 수 없습니다."),


    // 제품
    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "정보를 찾을 수 없습니다."),

    // 주문
    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "주문 정보를 찾을 수 없습니다."),
    ORDER_NOT_CANCLE(HttpStatus.CONFLICT, "취소 불가능 상태입니다."),

    // 주소
    ADDRESS_NOT_FOUND(HttpStatus.BAD_REQUEST, "주소 정보를 찾을 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String message;

    ExceptionCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
