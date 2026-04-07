package com.build.ecommerce.core.web.dto;

import org.springframework.validation.FieldError;

public record ValidationErrorResponse (
        String field,
        String message
) {

    public static class ValidationErrorResponseBuilder {
        private String field;
        private String message;

        public ValidationErrorResponseBuilder field(String field) {
            this.field = field;
            return this;
        }

        public ValidationErrorResponseBuilder message(String message) {
            this.message = message;
            return this;
        }

        public ValidationErrorResponse build() {
            return new ValidationErrorResponse(field, message);
        }
    }

    public static ValidationErrorResponseBuilder builder() {
        return new ValidationErrorResponseBuilder();
    }

    public static ValidationErrorResponse toDto(FieldError fieldError) {
        return ValidationErrorResponse.builder()
                .field(fieldError.getField())
                .message(fieldError.getDefaultMessage())
                .build();
    }
}
