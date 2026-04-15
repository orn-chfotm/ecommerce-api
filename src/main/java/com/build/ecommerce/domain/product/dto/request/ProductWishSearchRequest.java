package com.build.ecommerce.domain.product.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record ProductWishSearchRequest(
        @NotNull(message = "찜한 제품을 선택해주세요.")
        @Schema(description = "찜하기 제품 id (PK)")
        Long productWishId
) {
}
