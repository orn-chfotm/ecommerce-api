package com.build.ecommerce.core.exception;

import com.build.ecommerce.core.dto.response.FailResponse;
import com.build.ecommerce.core.error.ApplicationException;
import com.build.ecommerce.core.error.ExceptionCode;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<FailResponse<Void>> handleException(ApplicationException exception) {
        return FailResponse.toResponse(exception.getExceptionCode());
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<FailResponse<List<ValidationErrorResponse>>> handleBindValidationException(BindException exception) {
        List<ValidationErrorResponse> validErrorList = exception.getFieldErrors().stream()
                .map(fieldError -> {
                    return ValidationErrorResponse.builder()
                            .field(fieldError.getField())
                            .message(fieldError.getDefaultMessage())
                            .build();
                })
                .collect(Collectors.toList());

        return FailResponse.toResponse(ExceptionCode.VALIDATION_EXCEPTION, validErrorList);
    }

    private record ValidationErrorResponse(String field, String message) {

        @Builder
        private ValidationErrorResponse {
        }

    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<FailResponse<Void>> handleException(RuntimeException exception) {
        return FailResponse.toResponse(ExceptionCode.EXCEPTION);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<FailResponse<Void>> handleException(Exception exception) {
        return FailResponse.toResponse(ExceptionCode.EXCEPTION);
    }
}