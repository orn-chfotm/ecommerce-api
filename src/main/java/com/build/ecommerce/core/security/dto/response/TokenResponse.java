package com.build.ecommerce.core.security.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record TokenResponse(
        @Schema(description = "접근 토큰")
        String accessToken,
        @Schema(description = "갱신 토큰")
        String refreshToken
) {

    private static class TokenResponseBuilder {
        private String accessToken;
        private String refreshToken;

        public TokenResponseBuilder accessToken(String accessToken) {
                this.accessToken = accessToken;
                return this;
        }

        public TokenResponseBuilder refreshToken(String refreshToken) {
                this.refreshToken = refreshToken;
                return this;
        }

        public TokenResponse builde() {
                return new TokenResponse(accessToken, refreshToken);
        }
    }

    private static TokenResponseBuilder builder() {
        return new TokenResponseBuilder();
    }

    public static TokenResponse toResposne(String accessToken, String refreshToken) {
         return TokenResponse.builder()
                 .accessToken(accessToken)
                 .refreshToken(refreshToken)
             .builde();
    }
}
