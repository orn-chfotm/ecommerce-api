package com.build.ecommerce.domain.admin.dto.request;

import com.build.ecommerce.domain.admin.entity.Admin;
import com.build.ecommerce.domain.admin.entity.AdminRole;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.security.crypto.password.PasswordEncoder;

public record AdminRequest(
        @NotBlank(message = "이메일을 입력해야 합니다.")
        @Schema(description = "관리자 email(ID)")
        String email,
        @NotBlank(message = "비밀번호를 입력해야 합니다.")
        @Schema(description = "관리자 PW")
        String password,
        @NotBlank(message = "이름을 입력해야 합니다.")
        @Schema(description = "관리자 이름")
        String name,
        @NotNull(message = "다시 시도해주세요")
        @Schema(description = "관리자 권한")
        String role
) {

    public static Admin toEntity(AdminRequest request, PasswordEncoder passwordEncoder) {
        return Admin.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password))
                .name(request.name)
                .role(AdminRole.getByValue(request.role))
                .build();
    }
}
