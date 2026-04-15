package com.build.ecommerce.core.web.dto;

import lombok.Builder;
import org.springframework.validation.FieldError;

@Builder
public record ValidationErrorResponse (
        String field,
        String message
) {
    public static ValidationErrorResponse toDto(FieldError fieldError) {
        return ValidationErrorResponse.builder()
                .field(fieldError.getField())
                .message(fieldError.getDefaultMessage())
                .build();
    }
}
