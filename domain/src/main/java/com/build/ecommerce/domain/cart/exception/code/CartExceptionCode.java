package com.build.ecommerce.domain.cart.exception.code;

import com.build.ecommerce.core.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CartExceptionCode implements ErrorCode {

    CART_NOT_FOUND(HttpStatus.NOT_FOUND, "장바구니 정보를 찾을 수 없습니다.")
    ;

    private final HttpStatus httpStatus;
    private final String message;
}
