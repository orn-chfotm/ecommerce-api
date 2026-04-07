package com.build.ecommerce.domain.order.dto.response;

import com.build.ecommerce.domain.product.entity.Product;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

public record OrderedProductDeatilResponse(
        @Schema(description = "제품 카테고리")
        String category,
        @Schema(description = "제품명")
        String name,
        @Schema(description = "제품 설명")
        String description,
        @Schema(description = "제품 가격")
        BigDecimal price
) {

    private static class OrderProductDeatilResponseBuilder {
        private String category;
        private String name;
        private String description;
        private BigDecimal price;

        public OrderProductDeatilResponseBuilder category(String category) {
            this.category = category;
            return this;
        }

        public OrderProductDeatilResponseBuilder name(String name) {
            this.name = name;
            return this;
        }

        public OrderProductDeatilResponseBuilder description(String description) {
            this.description = description;
            return this;
        }

        public OrderProductDeatilResponseBuilder price(BigDecimal price) {
            this.price = price;
            return this;
        }

        public OrderedProductDeatilResponse build() {
            return new OrderedProductDeatilResponse(category, name, description, price);
        }
    }

    public static OrderProductDeatilResponseBuilder builder() {
        return new OrderProductDeatilResponseBuilder();
    }

    public static OrderedProductDeatilResponse toDto(Product product) {
        return OrderedProductDeatilResponse.builder()
                .category(product.getCategory().name())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .build();
    }
}
