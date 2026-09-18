package com.build.ecommerce.core.exception.handler;

import com.build.ecommerce.core.exception.ApplicationException;
import com.build.ecommerce.core.exception.code.ExceptionCode;
import com.build.ecommerce.core.response.FailResponse;
import com.build.ecommerce.core.response.ValidationErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BindException.class)
    public ResponseEntity<FailResponse<List<ValidationErrorResponse>>> handleBindValidationException(BindException exception) {
        List<ValidationErrorResponse> validErrorList = exception.getFieldErrors().stream()
                .map(ValidationErrorResponse::toDto)
                .collect(Collectors.toList());

        return FailResponse.toResponse(ExceptionCode.VALIDATION_EXCEPTION, validErrorList);
    }

    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<FailResponse<Void>> handleException(ApplicationException exception) {
        return FailResponse.toResponse(exception);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<FailResponse<Void>> handleAccessDeniedException(AccessDeniedException exception) {
        return FailResponse.toResponse(ExceptionCode.AUTHENTICATION_FORBIDDEN);
    }

    /**
     * 비관적 락 획득 실패(락 대기 타임아웃 등) 전역 안전망.
     * RuntimeException 핸들러보다 구체 타입이라 Spring이 이 핸들러를 우선 매칭한다.
     */
    @ExceptionHandler({CannotAcquireLockException.class, PessimisticLockingFailureException.class})
    public ResponseEntity<FailResponse<Void>> handleLockAcquireFailureException(PessimisticLockingFailureException exception) {
        return FailResponse.toResponse(ExceptionCode.LOCK_ACQUIRE_FAILED);
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
