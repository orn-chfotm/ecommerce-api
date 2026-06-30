package com.build.ecommerce.domain.product.dto.response;

import com.build.ecommerce.infra.file.entity.FileDetail;

public record FileDetailResponse(
        Long fileDetailId,
        String originalFileName,
        String accessUrl,
        Integer sortOrder
) {
    public static FileDetailResponse toDto(FileDetail fileDetail) {
        return new FileDetailResponse(
                fileDetail.getId(),
                fileDetail.getOriginalFileName(),
                fileDetail.getPath(),
                fileDetail.getSortOrder()
        );
    }
}
