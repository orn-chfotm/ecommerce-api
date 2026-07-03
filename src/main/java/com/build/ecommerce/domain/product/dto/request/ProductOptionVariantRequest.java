package com.build.ecommerce.domain.product.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.List;

public record ProductOptionVariantRequest(
        @Size(max = 100, message = "SKU는 {max}자 이하로 입력해야 합니다.")
        @Schema(description = "판매자 관리용 SKU 코드 (선택)")
        String sku,

        @NotNull(message = "옵션 조합 재고 수량을 입력해야 합니다.")
        @Min(value = 0, message = "옵션 조합 재고 수량은 {value} 이상을 입력해야 합니다.")
        @Schema(description = "옵션 조합 재고 수량")
        Integer stockQuantity,

        @DecimalMin(value = "0", message = "옵션 조합 추가 금액은 {value}원 이상을 입력해야 합니다.")
        @Schema(description = "옵션 조합 추가 금액 (미입력 시 0)")
        BigDecimal priceDelta,

        @Min(value = 1, message = "옵션 조합별 최대 구매 수량은 {value} 이상을 입력해야 합니다.")
        @Schema(description = "옵션 조합별 최대 구매 수량 (선택)")
        Integer maxPurchaseQuantity,

        @NotEmpty(message = "옵션 조합을 구성하는 옵션 값을 입력해야 합니다.")
        @Valid
        @Schema(description = "옵션 조합을 구성하는 옵션 값 목록 (예: 색상=블랙, 사이즈=M)")
        List<ProductOptionVariantValueRequest> optionValues
) {
}
