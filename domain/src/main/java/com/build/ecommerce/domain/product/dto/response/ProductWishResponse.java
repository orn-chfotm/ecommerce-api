package com.build.ecommerce.domain.product.dto.response;

import com.build.ecommerce.domain.product.entity.ProductWish;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record ProductWishResponse (
        @Schema(description = "제품 찜하기 PK")
        Long productWishId,
        @Schema(description = "찜한 제품 정보")
        ProductResponse product
) {
    /**
     * 단건 조회용 - product 내부 fileMaster.getFileDetailList()를 바로 lazy loading 한다.
     */
    public static ProductWishResponse toDto(ProductWish productWish) {
        return ProductWishResponse.builder()
                .productWishId(productWish.getId())
                .product(ProductResponse.toDto(productWish.getProduct()))
                .build();
    }

    /**
     * 목록 조회용 - files는 Service에서 fileMaster id들을 모아 배치 조회한 결과를 주입받는다.
     */
    public static ProductWishResponse toDto(ProductWish productWish, List<FileDetailResponse> files) {
        return ProductWishResponse.builder()
                .productWishId(productWish.getId())
                .product(ProductResponse.toDto(productWish.getProduct(), files))
                .build();
    }
}