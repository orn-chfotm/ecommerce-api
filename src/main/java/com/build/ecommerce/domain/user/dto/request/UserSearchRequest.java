package com.build.ecommerce.domain.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record UserSearchRequest(
        @Schema(description = "이메일(ID)")
        String email,
        @Schema(description = "이름")
        String name,
        @Schema(description = "성별")
        String gender
) {

}
