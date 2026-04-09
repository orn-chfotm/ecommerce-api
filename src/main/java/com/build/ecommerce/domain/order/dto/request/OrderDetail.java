package com.build.ecommerce.domain.order.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record OrderDetail (
        @NotNull(message = "제품 번호가 정확하지 않습니다.")
        @Schema(name = "제품 PK")
        Long productId,
        @NotNull(message = "제품 수량을 입력해주세요")
        @Positive(message = "최소 수량 1개 이상 선택해주세요.")
        @Schema(name = "제품 수량")
        Integer quantity
) {

}
