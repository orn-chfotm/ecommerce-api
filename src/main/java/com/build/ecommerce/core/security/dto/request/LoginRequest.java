package com.build.ecommerce.core.security.dto.request;

import com.build.ecommerce.core.dto.response.ValidationErrorResponse;
import com.build.ecommerce.core.security.exception.AuthenticationFailException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.io.IOException;
import java.util.List;
import java.util.Set;

public record LoginRequest(
        @NotBlank(message = "ID를 입력해야 합니다.")
        @Schema(description = "로그인 ID")
        String email,
        @NotBlank(message = "PW를 입력해야 합니다.")
        @Schema(description = "로그인 PW")
        String password
) {

        public static LoginRequest parseDto(@NotNull HttpServletRequest request, @NotNull Validator validator) throws IOException {
                ObjectMapper objectMapper = new ObjectMapper();
                LoginRequest loginRequest = objectMapper.readValue(request.getInputStream(), LoginRequest.class);

                Set<ConstraintViolation<LoginRequest>> validated = validator.validate(loginRequest);
                if (!validated.isEmpty()) {
                        List<ValidationErrorResponse> errorList = validated.stream()
                                .map(violation -> ValidationErrorResponse.builder()
                                        .field(violation.getPropertyPath().toString())
                                        .message(violation.getMessage())
                                        .build()).toList();

                        throw new AuthenticationFailException(objectMapper.writeValueAsString(errorList));
                }

                return loginRequest;
        }
}
