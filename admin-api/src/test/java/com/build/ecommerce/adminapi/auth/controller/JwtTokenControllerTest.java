package com.build.ecommerce.adminapi.auth.controller;

import com.build.ecommerce.adminapi.helper.UnitTestHelper;
import com.build.ecommerce.core.exception.code.ExceptionCode;
import com.build.ecommerce.core.security.jwt.enums.TokenType;
import com.build.ecommerce.core.security.jwt.token.JwtPayload;
import com.build.ecommerce.core.security.jwt.token.JwtProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;

import java.util.Date;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class JwtTokenControllerTest extends UnitTestHelper {

    @Autowired
    private JwtProvider jwtProvider;

    @Test
    @DisplayName("만료된 AccessToken 으로 관리자 보호 API 를 호출하면 401 과 만료 메시지를 반환한다")
    void authenticateWithExpiredAccessToken() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + expiredAccessToken());

        mockMvc.perform(get("/v1/code-groups").headers(headers))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value(ExceptionCode.TOKEN_EXPIRED.getMessage()));
    }

    private String expiredAccessToken() {
        return jwtProvider.createToken(
                JwtPayload.builder()
                        .tokenType(TokenType.ACCESS)
                        .id(1L)
                        .authority("ADMIN")
                        .issuedAt(new Date())
                        .build(),
                -1000L
        );
    }
}
