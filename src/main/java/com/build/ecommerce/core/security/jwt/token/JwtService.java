package com.build.ecommerce.core.security.jwt.token;

import com.build.ecommerce.core.security.exception.extend.AuthenticationFailException;
import com.build.ecommerce.core.security.jwt.dto.request.TokenRequest;
import com.build.ecommerce.core.security.jwt.dto.response.TokenResponse;
import com.build.ecommerce.core.security.jwt.enums.TokenType;
import com.build.ecommerce.core.security.jwt.property.JwtProperty;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.websocket.AuthenticationException;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtProvider jwtProvider;
    private final JwtProperty jwtProperty;

    public TokenResponse createToken(JwtPayload jwtPayload) {
        String accessToken = jwtProvider.createToken(jwtPayload, jwtProperty.getAccessExpiration());
        String refreshToken = null;

        if (jwtPayload.authority().equals("USER")) {
            refreshToken = jwtProvider.createToken(jwtPayload, jwtProperty.getRefreshExpiration());
        }

        return TokenResponse.toResponse(accessToken, refreshToken);
    }

    public JwtPayload verifyToken(String jwtToken) {
        return jwtProvider.verifyToken(jwtToken);
    }

    /**
     * 사용자 Refresh 토큰을 사용한 Token 재발급
     * 관리자는 Refresh 토큰을 사용한 Token 재발급 제한 (로그인 토큰 발급 시 AccessToken 만 발급)
     */
    public TokenResponse getTokenRefresh(TokenRequest tokenRequest) {
        JwtPayload refreshJwtPayload = verifyToken(tokenRequest.refreshToken());

        if (refreshJwtPayload.tokenType() == TokenType.REFRESH) {
            throw new AuthenticationFailException("Refresh Token이 아닙니다.");
        }

        return TokenResponse.toResponse(
                jwtProvider.createToken(getPayload(refreshJwtPayload, TokenType.ACCESS), jwtProperty.getAccessExpiration()),
                jwtProvider.createToken(getPayload(refreshJwtPayload, TokenType.REFRESH), jwtProperty.getRefreshExpiration())
        );
    }

    public JwtPayload getPayload(JwtPayload jwtPayload, TokenType tokenType) {
        return JwtPayload.builder()
                .tokenType(tokenType)
                .id(jwtPayload.id())
                .authority(jwtPayload.authority())
                .issuedAt(new Date())
                .build();
    }

}
