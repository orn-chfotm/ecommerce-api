package com.build.ecommerce.domain.admin.dto.response;

import com.build.ecommerce.domain.admin.entity.Admin;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record AdminResponse(
        @Schema(description = "관리자 email(ID)")
        String email,
        @Schema(description = "관리자 이름")
        String name,
        @Schema(description = "관리자 권한")
        String role
) {
    public static AdminResponse toDto(Admin admin) {
        return AdminResponse.builder()
                .email(admin.getEmail())
                .name(admin.getName())
                .role(admin.getRole().name())
                .build();
    }
}
