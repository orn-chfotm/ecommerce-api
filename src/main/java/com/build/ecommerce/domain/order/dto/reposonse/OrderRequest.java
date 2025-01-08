package com.build.ecommerce.domain.order.dto.reposonse;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.util.List;

public record OrderRequest(
        @NotNull(message = "주문 목록을 입력해야 합니다.")
        @Schema(description = "주문 목록")
        List<OrderDetail> orders,
        @NotNull(message = "주문자 정보를 입력해야 합니다.")
        @Schema(description = "주문자 PK")
        Long userId,
        @NotNull(message = "배송지를 선택해야 합니다.")
        @Schema(description = "배송지")
        Long addressId
) {

}
