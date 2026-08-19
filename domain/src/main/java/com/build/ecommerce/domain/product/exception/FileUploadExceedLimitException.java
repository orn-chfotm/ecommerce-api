package com.build.ecommerce.domain.product.exception;

import com.build.ecommerce.core.exception.type.InvalidInputException;

public class FileUploadExceedLimitException extends InvalidInputException {

    private static final String DEFAULT_MESSAGE = "파일 업로드 최대 개수를 초과했습니다.";

    public FileUploadExceedLimitException() {
        super(DEFAULT_MESSAGE);
    }
}
