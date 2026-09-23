package com.build.ecommerce.domain.order.exception.code;

import com.build.ecommerce.core.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum OrderExceptionCode implements ErrorCode {

    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "주문 정보를 찾을 수 없습니다."),
    ORDER_STATUS_CONFLICT(HttpStatus.CONFLICT, "취소 불가능 상태입니다."),
    ORDER_OPTION_REQUIRED(HttpStatus.BAD_REQUEST, "등록되지 않은 옵션은 선택할 수 없습니다.")
    ;

    private final HttpStatus httpStatus;
    private final String message;
}
