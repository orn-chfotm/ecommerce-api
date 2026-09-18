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
    FILE_UPLOAD_EXCEED_LIMIT(HttpStatus.BAD_REQUEST, "파일 업로드 최대 개수를 초과했습니다."),
    ADD_ON_PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "추가구성상품 정보를 찾을 수 없습니다."),
    ADD_ON_TARGET_NOT_ADD_ON_TYPE(HttpStatus.BAD_REQUEST, "추가구성상품 유형의 상품만 추가구성으로 등록할 수 있습니다."),
    ADD_ON_PARENT_NOT_NORMAL_TYPE(HttpStatus.BAD_REQUEST, "일반 상품에만 추가구성상품을 등록할 수 있습니다."),
    ADD_ON_SELF_REFERENCE_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "자기 자신을 추가구성상품으로 등록할 수 없습니다."),
    ADD_ON_TARGET_HAS_OPTIONS(HttpStatus.BAD_REQUEST, "옵션이 등록된 상품은 추가구성상품으로 등록할 수 없습니다."),
    ADD_ON_ALREADY_REGISTERED(HttpStatus.CONFLICT, "이미 등록된 추가구성상품입니다."),
    ADD_ON_NOT_MAPPED(HttpStatus.BAD_REQUEST, "해당 상품에 등록되지 않은 추가구성상품입니다."),
    ADD_ON_DUPLICATED(HttpStatus.BAD_REQUEST, "같은 추가구성상품을 중복으로 선택할 수 없습니다."),
    ADD_ON_PRODUCT_NOT_ORDERABLE_ALONE(HttpStatus.BAD_REQUEST, "추가구성상품은 단독으로 주문할 수 없습니다."),
    ADD_ON_PRODUCT_NOT_ALLOWED_IN_CART(HttpStatus.BAD_REQUEST, "추가구성상품은 장바구니에 담을 수 없습니다."),
    ADD_ON_PRODUCT_NOT_ALLOWED_IN_WISH(HttpStatus.BAD_REQUEST, "추가구성상품은 찜할 수 없습니다."),
    ADD_ON_PRODUCT_OPTION_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "추가구성상품에는 옵션을 등록할 수 없습니다."),
    PRODUCT_NOT_DISPLAYED(HttpStatus.CONFLICT, "현재 노출되지 않는 상품입니다."),
    PRODUCT_NOT_ORDERABLE(HttpStatus.CONFLICT, "현재 주문할 수 없는 상품입니다.")
    ;

    private final HttpStatus httpStatus;
    private final String message;
}
