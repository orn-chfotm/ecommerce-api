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
    ORDER_LOCK_ACQUIRE_FAILED(HttpStatus.CONFLICT, "주문 처리가 지연되고 있습니다. 잠시 후 다시 시도해주세요.")
    ;

    private final HttpStatus httpStatus;
    private final String message;
}
