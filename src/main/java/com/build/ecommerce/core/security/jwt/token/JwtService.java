package com.build.ecommerce.core.security.jwt.token;

import com.build.ecommerce.core.security.jwt.dto.request.TokenRequest;
import com.build.ecommerce.core.security.jwt.dto.response.TokenResponse;
import com.build.ecommerce.core.security.jwt.property.JwtProperty;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtProvider jwtProvider;
    private final JwtProperty jwtProperty;

    public TokenResponse createToken(JwtPayload jwtPayload) {
        String accessToken = jwtProvider.createToken(jwtPayload, jwtProperty.getAccessExpiration());
        String refreshToken = null;

        if (jwtPayload.Authority().equals("ROLE_USER")) {
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
    public TokenResponse getTokenRefresh(TokenRequest request) {
        JwtPayload jwtPayload = verifyToken(request.accessToken());

        return TokenResponse.toResponse(
                jwtProvider.createToken(jwtPayload, jwtProperty.getAccessExpiration()),
                jwtProvider.createToken(jwtPayload, jwtProperty.getRefreshExpiration())
        );
    }
}
