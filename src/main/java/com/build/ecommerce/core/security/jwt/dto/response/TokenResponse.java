package com.build.ecommerce.core.security.jwt.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record TokenResponse(
        @Schema(description = "접근 토큰")
        String accessToken,
        @Schema(description = "갱신 토큰")
        String refreshToken
) {
    public static TokenResponse toResponse(String accessToken, String refreshToken) {
         return TokenResponse.builder()
                 .accessToken(accessToken)
                 .refreshToken(refreshToken)
             .build();
    }
}
