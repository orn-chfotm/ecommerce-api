package com.build.ecommerce.domain.product.dto.request;

import com.build.ecommerce.domain.product.enums.ProductCategoryType;
import com.build.ecommerce.domain.product.entity.Product;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

public record ProductRequest(
        @NotNull(message = "제품 카테고리를 입력해야 합니다.")
        @Schema(description = "제품 카테고리")
        ProductCategoryType category,

        @NotBlank(message = "제품명을 입력해야 합니다.")
        @Size(max = 200, message = "제품명은 {max}자 이하로 입력해야 합니다.")
        @Schema(description = "제품명")
        String name,

        @Size(max = 2000, message = "제품 설명은 {max}자 이하로 입력해야 합니다.")
        @Schema(description = "제품 설명")
        String description,

        @NotNull(message = "제품 가격을 입력해야 합니다.")
        @DecimalMin(value = "0", message = "제품 가격은 {value}원 이상을 입력해야 합니다. ")
        @Schema(description = "제품 가격")
        BigDecimal price,

        @NotNull(message = "제품 재고 수량을 입력해야 합니다.")
        @Min(value = 0, message = "제품 재고 수량을 {value} 이상을 입력해야 합니다.")
        @Schema(description = "제품 재고 수량")
        Integer stockQuantity,

        @NotNull(message = "최소 주문 수량을 입력해주세요.")
        @Min(value = 1, message = "최소 주문 수량은 {value} 이상을 입력해야 합니다.")
        @Schema(description = "최소 주문 수량")
        Integer minOrderQuantity,

        @NotNull(message = "노출 여부를 선택해주세요.")
        @Schema(description = "제품 노출 여부")
        Boolean active,

        @Schema(description = "제품 썸네일")
        MultipartFile productThumbnail
) {
    public Product toEntity() {
        return Product.builder()
                .category(category)
                .name(name)
                .description(description)
                .price(price)
                .stockQuantity(stockQuantity)
                .minOrderQuantity(minOrderQuantity)
                .active(active)
            .build();
    }
}
