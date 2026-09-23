package com.build.ecommerce.core.security.jwt.exception;

import com.build.ecommerce.core.exception.ApplicationException;
import com.build.ecommerce.core.exception.ErrorCode;

/**
 * JWT 토큰 검증 실패 예외.
 * access 인증(필터)과 refresh 재발급(MVC) 두 경로가 같은 검증 로직을 공유하므로
 * Spring Security 타입이 아닌 공통 앱 예외로 던진다.
 * 실패 사유는 별도 클래스가 아니라 ErrorCode 로 구분한다.
 */
public class TokenException extends ApplicationException {

    public TokenException(ErrorCode errorCode) {
        super(errorCode);
    }

    public TokenException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
}
