package com.build.ecommerce.domain.product.dto.response;

import com.build.ecommerce.domain.product.entity.Product;
import com.build.ecommerce.domain.product.enums.ProductCategoryType;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record ProductResponse(
        @Schema(description = "제품 ID(PK)")
        Long productId,
        @Schema(description = "제품 카테고리")
        ProductCategoryType category,
        @Schema(description = "제품명")
        String name,
        @Schema(description = "제품 설명")
        String description,
        @Schema(description = "제품 가격")
        BigDecimal price,
        @Schema(description = "제품 재고 수량")
        Integer stockQuantity,
        @Schema(description = "최소 주문 수량")
        Integer minOrderQuantity,
        @Schema(description = "제품 노출 여부")
        Boolean active,
        @Schema(description = "첨부파일 목록")
        List<FileDetailResponse> files
) {
    public static ProductResponse toDto(Product product) {
        List<FileDetailResponse> files = null;
        if (product.getFileMaster() != null) {
            files = product.getFileMaster().getFileDetailList().stream()
                    .map(FileDetailResponse::toDto)
                    .collect(Collectors.toList());
        }
        return ProductResponse.builder()
                .productId(product.getId())
                .category(product.getCategory())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stockQuantity(product.getStockQuantity())
                .minOrderQuantity(product.getMinOrderQuantity())
                .active(product.isActive())
                .files(files)
                .build();
    }

    public static ProductResponse toCreateDto(Long productId) {
        return ProductResponse.builder()
                .productId(productId)
                .build();
    }
}
