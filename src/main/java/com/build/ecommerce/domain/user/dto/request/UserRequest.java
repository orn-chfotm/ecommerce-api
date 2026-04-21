package com.build.ecommerce.domain.user.dto.request;

import com.build.ecommerce.common.support.time.LocalDateUtil;
import com.build.ecommerce.domain.user.enums.GenderType;
import com.build.ecommerce.domain.user.entity.User;
import com.build.ecommerce.domain.user.enums.UserRoleType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import org.springframework.security.crypto.password.PasswordEncoder;

public record UserRequest(
        @NotBlank(message = "이메일을 입력해야 합니다.")
        @Schema(description = "이메일(ID)")
        String email,
        @NotBlank(message = "비밀번호를 입력해야 합니다.")
        @Schema(description = "비밀번호")
        String password,
        @NotBlank(message = "이름을 입력해야 합니다.")
        @Schema(description = "이름")
        String name,
        @NotBlank(message = "성별을 입력해야 합니다.")
        @Schema(description = "성별")
        String gender,
        @NotBlank(message = "생년월일을 입력해야 합니다.")
        @Schema(description = "생년월일")
        String birthDate
) {

    public static User toEntity(final UserRequest userRequest, final PasswordEncoder passwordEncoder) {
        return User.builder()
                .email(userRequest.email)
                .password(passwordEncoder.encode(userRequest.password))
                .name(userRequest.name)
                .gender(GenderType.getByValue(userRequest.gender))
                .birthDate(LocalDateUtil.toLocalDate(userRequest.birthDate))
                .role(UserRoleType.USER)
            .build();
    }
}
