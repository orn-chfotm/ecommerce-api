
package com.build.ecommerce.domain.user.dto.response;

import com.build.ecommerce.common.support.time.LocalDateUtil;
import com.build.ecommerce.domain.user.entity.User;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
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
