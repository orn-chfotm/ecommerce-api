package com.build.ecommerce.domain.admin.exception.code;

import com.build.ecommerce.core.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AdminExceptionCode implements ErrorCode {

    ADMIN_NOT_FOUND(HttpStatus.NOT_FOUND, "관리자 정보를 찾을 수 없습니다."),
    ADMIN_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 존재하는 관리자입니다.")
    ;

    private final HttpStatus httpStatus;
    private final String message;
}
