package com.build.ecommerce.domain.order.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record OrderAddOnDetail(
        @NotNull(message = "추가구성상품 번호가 정확하지 않습니다.")
        @Schema(description = "추가구성상품 PK")
        Long productId,

        @NotNull(message = "추가구성상품 수량을 입력해주세요")
        @Min(value = 1, message = "최소 수량 {value}개 이상 선택해주세요.")
        @Schema(description = "추가구성상품 수량")
        Integer quantity
) {

}
