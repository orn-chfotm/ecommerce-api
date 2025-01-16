package com.build.ecommerce.core.exception.handler;

import com.build.ecommerce.core.dto.response.FailResponse;
import com.build.ecommerce.core.dto.response.ValidationErrorResponse;
import com.build.ecommerce.core.exception.ApplicationException;
import com.build.ecommerce.core.exception.code.ExceptionCode;
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
                .map(ValidationErrorResponse::toDto)
                .collect(Collectors.toList());

        return FailResponse.toResponse(ExceptionCode.VALIDATION_EXCEPTION, validErrorList);
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
