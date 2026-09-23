package com.build.ecommerce.core.exception.type;

import com.build.ecommerce.core.exception.ApplicationException;
import com.build.ecommerce.core.exception.ErrorCode;
import com.build.ecommerce.core.exception.code.ExceptionCode;

import static com.build.ecommerce.core.exception.code.ExceptionCode.CONFLICT;

public class NotAllowedException extends ApplicationException {
    public NotAllowedException() {
        super(CONFLICT);
    }

    public NotAllowedException(ErrorCode errorCode) {
        super(errorCode);
    }

    public NotAllowedException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
