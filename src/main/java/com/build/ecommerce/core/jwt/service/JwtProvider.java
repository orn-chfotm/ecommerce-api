package com.build.ecommerce.core.jwt.service;

import com.build.ecommerce.core.config.property.JwtProperty;
import com.build.ecommerce.core.jwt.JwtPayload;
import com.build.ecommerce.core.security.exception.AuthenticationFailException;
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
    private final static String USERID_KEY = "user-id";
    private final SecretKey secretKey;

    public JwtProvider(JwtProperty jwtProperty) {
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(jwtProperty.getKey()));
    }

    public String createToken(JwtPayload jwtPayload, long expiration) {
        return Jwts.builder()
                .claim(USERID_KEY, Objects.requireNonNull(String.valueOf(jwtPayload.id())))
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
            String id = payload.get(USERID_KEY, String.class);

            return new JwtPayload(Long.parseLong(id), null, issuedAt);
        } catch (ExpiredJwtException e) {
            throw new AuthenticationFailException("인증 토큰 만료이 만료되었습니다.");
        } catch (JwtException e) {
            throw new AuthenticationFailException("사용자 인증에 실패했습니다.");
        }
    }
}
