package com.build.ecommerce.core.response;

import com.build.ecommerce.core.exception.ApplicationException;
import com.build.ecommerce.core.exception.ErrorCode;
import com.build.ecommerce.core.exception.code.ExceptionCode;
import com.build.ecommerce.core.support.time.LocalDateTimeUtil;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@JsonInclude(value = JsonInclude.Include.NON_NULL)
public record FailResponse<T> (
        @Schema(description = "요청시간")
        String timestamp,
        @Schema(description = "상태코드")
        Integer status,
        @Schema(description = "상태코드")
        String code,
        @Schema(description = "상세 메세지")
        String message,
        @Schema(description = "상세 데이터")
        T data
) implements BaseResponse {

    public static FailResponse<Void> of(final String message) {
        return new FailResponse<>(
                LocalDateTimeUtil.nowToString(),
                null,
                null,
                message,
                null
        );
    }

    public static <T> ResponseEntity<FailResponse<Void>> toResponse(@NotNull final ErrorCode errorCode) {
        HttpStatus httpStatus = errorCode.getHttpStatus();
        return ResponseEntity.status(errorCode.getHttpStatus())
                .body(new FailResponse<>(
                        LocalDateTimeUtil.nowToString(),
                        httpStatus.value(),
                        httpStatus.name(),
                        errorCode.getMessage(),
                        null
                ));
    }

    public static ResponseEntity<FailResponse<Void>> toResponse(@NotNull ApplicationException exception) {
        ErrorCode errorCode = exception.getErrorCode();
        String message = exception.getMessage();

        HttpStatus httpStatus = errorCode.getHttpStatus();
        String responseMessage = (message == null || message.isBlank())
                ? errorCode.getMessage()
                : message;

        return ResponseEntity.status(errorCode.getHttpStatus())
                .body(new FailResponse<>(
                        LocalDateTimeUtil.nowToString(),
                        httpStatus.value(),
                        httpStatus.name(),
                        responseMessage,
                        null
                ));
    }

    public static <T> ResponseEntity<FailResponse<T>> toResponse(@NotNull final ExceptionCode exceptionCode,
                                                                 final T data) {
        HttpStatus httpStatus = exceptionCode.getHttpStatus();
        return ResponseEntity.status(exceptionCode.getHttpStatus())
                .body(new FailResponse<>(
                        LocalDateTimeUtil.nowToString(),
                        httpStatus.value(),
                        httpStatus.name(),
                        exceptionCode.getMessage(),
                        data
                ));
    }
}
