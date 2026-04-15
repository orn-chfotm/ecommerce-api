package com.build.ecommerce.domain.product.dto.request;

import com.build.ecommerce.domain.product.enums.ProductCategoryType;
import io.swagger.v3.oas.annotations.media.Schema;

public record ProductSearchRequest (
        @Schema(description = "카테고리")
        ProductCategoryType category,
        @Schema(description = "제품 명")
        String name,
        @Schema(description = "최소 금액")
        Integer minPrice,
        @Schema(description = "최대 금액")
        Integer maxPrice,
        @Schema(description = "재고량")
        Integer stockQuantity
) {
        public ProductCategoryType getCategory() {
                return this.category != null ? category : null;
        }
}
