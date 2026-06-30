package com.build.ecommerce.core.security.jwt.token;

import com.build.ecommerce.core.security.exception.extend.AuthenticationFailException;
import com.build.ecommerce.core.security.jwt.enums.TokenType;
import com.build.ecommerce.core.security.jwt.property.JwtProperty;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Objects;

@Component
public class JwtProvider {

    @Value("${spring.application.name}")
    private String issuer;
    private final SecretKey secretKey;

    public JwtProvider(JwtProperty jwtProperty) {
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(jwtProperty.getKey()));
    }

    public String createToken(JwtPayload jwtPayload, long expiration) {
        return Jwts.builder()
                .claim("id", Objects.requireNonNull(String.valueOf(jwtPayload.id())))
                .claim("authority", Objects.requireNonNull(String.valueOf(jwtPayload.authority())))
                .claim("tokenType", jwtPayload.tokenType().name())
                .issuer(issuer)
                .issuedAt(Objects.requireNonNull(jwtPayload.issuedAt()))
                .expiration(new Date(jwtPayload.issuedAt().getTime() + expiration))
                .signWith(secretKey, Jwts.SIG.HS256)
                .compact();
    }

    public JwtPayload verifyToken(String jwtToken) {
        try {
            Jws<Claims> claimsJws = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(jwtToken);
            Claims payload = claimsJws.getPayload();

            Date issuedAt = payload.getIssuedAt();
            String id = payload.get("id", String.class);
            String authority = payload.get("authority", String.class);
            TokenType tokenType = TokenType.valueOf(payload.get("tokenType", String.class));

            return new JwtPayload(tokenType, Long.parseLong(id), authority, issuedAt);
        } catch (ExpiredJwtException e) {
            throw new AuthenticationFailException("인증 토큰 만료이 만료되었습니다.");
        } catch (JwtException e) {
            throw new AuthenticationFailException("사용자 인증에 실패했습니다.");
        }
    }
}
