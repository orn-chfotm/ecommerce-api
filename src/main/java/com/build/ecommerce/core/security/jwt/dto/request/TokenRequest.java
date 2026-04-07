package com.build.ecommerce.core.security.jwt.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record TokenRequest(
        @NotBlank
        @Schema(description = "access token")
        String accessToken,
        @NotBlank
        @Schema(description = "refresh token")
        String refreshToken
) {

}
