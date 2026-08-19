package com.build.ecommerce.domain.order.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record OrderedDetail(
        OrderedProductResponse orderedProductResponse,
        OrderedProductDetailResponse orderedProductDetailResponse
) {
    public static OrderedDetail toDto(OrderedProductResponse orderedProductResponse,
                                      OrderedProductDetailResponse orderedProductDetailResponse) {
        return OrderedDetail.builder()
                .orderedProductResponse(orderedProductResponse)
                .orderedProductDetailResponse(orderedProductDetailResponse)
                .build();
    }

}
