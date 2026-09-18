package com.build.ecommerce.domain.order.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

public record OrderDetail (
        @NotNull(message = "제품 번호가 정확하지 않습니다.")
        @Schema(name = "제품 PK")
        Long productId,

        @Schema(name = "제품 옵션 조합(SKU) PK, 옵션이 등록된 상품이면 필수")
        Long productOptionVariantId,

        @NotNull(message = "제품 수량을 입력해주세요")
        @Min(value = 1, message = "최소 수량 {value}개 이상 선택해주세요.")
        @Schema(name = "제품 수량")
        Integer quantity,

        @Valid
        @Schema(description = "추가구성상품 목록 (선택)")
        List<OrderAddOnDetail> addOns
) {

}
