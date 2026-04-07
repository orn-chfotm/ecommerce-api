
package com.build.ecommerce.domain.user.dto.response;

import com.build.ecommerce.core.support.time.LocalDateUtil;
import com.build.ecommerce.domain.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

public record UserResponse(
        @Schema(description = "User table PK")
        Long id,
        @Schema(description = "이메일(ID)")
        String email,
        @Schema(description = "이름")
        String name,
        @Schema(description = "성별")
        String gender,
        @Schema(description = "생년월일")
        String birthDate
) {

    @Builder
    public UserResponse {
    }

    public static UserResponse toDto(final User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .gender(user.getGender().getValue())
                .birthDate(LocalDateUtil.toString(user.getBirthDate()))
            .build();
    }
}
