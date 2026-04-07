package com.build.ecommerce.domain.product.dto.request;

import com.build.ecommerce.domain.product.entity.Category;
import io.swagger.v3.oas.annotations.media.Schema;

public record ProductSearchRequest (
        @Schema(description = "카테고리")
        String category,
        @Schema(description = "제품 명")
        String name,
        @Schema(description = "최소 금액")
        Integer minPrice,
        @Schema(description = "최대 금액")
        Integer maxPrice,
        @Schema(description = "재고량")
        Integer stockQuantity
) {
        public Category getCategory() {
                return this.category != null ? Category.getByValue(category) : null;
        }

}
