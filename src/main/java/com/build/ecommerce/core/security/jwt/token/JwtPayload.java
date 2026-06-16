package com.build.ecommerce.core.security.jwt.token;

import com.build.ecommerce.core.security.jwt.enums.TokenType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.Date;

@Builder
public record JwtPayload(
        @Schema(description = "발급 토큰 타입")
        TokenType tokenType,
        @Schema(description = "회원/관리자 PK")
        Long id,
        @Schema(description = "회원/관리자 권한")
        String authority,
        @Schema(description = "인증 일시")
        Date issuedAt
) {
        public String getRoleSecured() {
                if (this.authority == null || this.authority.isBlank()) return "";

                return String.format("ROLE_%s", this.authority);
        }
}
