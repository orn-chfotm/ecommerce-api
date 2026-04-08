package com.build.ecommerce.domain.product.dto.request;

import com.build.ecommerce.domain.product.entity.ProductWish;
import com.build.ecommerce.domain.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record ProductWishRequest (
        @NotNull(message = "찜하기 제품을 선택해주세요.")
        @Schema(description = "선택 제품 id (PK)")
        Long productId
) {
}
