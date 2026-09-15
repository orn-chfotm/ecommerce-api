package com.build.ecommerce.domain.code.exception.code;

import com.build.ecommerce.core.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum CodeExceptionCode implements ErrorCode {

    CODE_GROUP_NOT_FOUND(HttpStatus.NOT_FOUND, "코드 그룹 정보를 찾을 수 없습니다."),
    CODE_DETAIL_NOT_FOUND(HttpStatus.NOT_FOUND, "상세 코드 정보를 찾을 수 없습니다."),
    CODE_DETAIL_SORT_ORDER_OUT_OF_RANGE(HttpStatus.BAD_REQUEST, "정렬 순서 범위를 확인해주세요.")
    ;

    private final HttpStatus httpStatus;
    private final String message;
}
