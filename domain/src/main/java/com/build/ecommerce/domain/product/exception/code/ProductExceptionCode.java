package com.build.ecommerce.domain.product.exception.code;

import com.build.ecommerce.core.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ProductExceptionCode implements ErrorCode {

    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "제품 정보를 찾을 수 없습니다."),
    PRODUCT_OPTION_VARIANT_NOT_FOUND(HttpStatus.NOT_FOUND, "상품 옵션 조합 정보를 찾을 수 없습니다."),
    PRODUCT_WISH_NOT_FOUND(HttpStatus.NOT_FOUND, "찜하기 제품 정보를 찾을 수 없습니다."),
    PRODUCT_NOT_ENOUGH_STOCK(HttpStatus.CONFLICT, "주문 상품의 재고가 부족합니다."),
    PRODUCT_OPTION_ALREADY_REGISTERED(HttpStatus.CONFLICT, "이미 옵션이 등록된 상품입니다."),
    PRODUCT_OPTION_NOT_REGISTERED(HttpStatus.BAD_REQUEST, "등록되지 않은 옵션입니다."),
    PRODUCT_CATEGORY_NOT_FOUND(HttpStatus.BAD_REQUEST, "제품 카테고리 정보를 찾을 수 없습니다."),
    FILE_UPLOAD_EXCEED_LIMIT(HttpStatus.BAD_REQUEST, "파일 업로드 최대 개수를 초과했습니다."),
    FILE_EXTENSION_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "허용되지 않는 파일 형식입니다.")
    ;

    private final HttpStatus httpStatus;
    private final String message;
}
