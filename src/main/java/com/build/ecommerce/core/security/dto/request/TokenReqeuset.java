package com.build.ecommerce.core.security.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record TokenReqeuset(
        @NotBlank
        @Schema(description = "access token")
        String accessToken,
        @NotBlank
        @Schema(description = "refresh token")
        String refreshToken
) {

}
