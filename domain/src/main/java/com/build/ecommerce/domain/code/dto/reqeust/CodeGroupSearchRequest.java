package com.build.ecommerce.domain.code.dto.reqeust;

import io.swagger.v3.oas.annotations.media.Schema;

public record CodeGroupSearchRequest(
        @Schema(name = "코드 id")
        String id,
        @Schema(name = "코드")
        String code
) {
}