package com.build.ecommerce.userapi.auth.controller;

import com.build.ecommerce.core.exception.code.ExceptionCode;
import com.build.ecommerce.core.security.jwt.dto.request.TokenRequest;
import com.build.ecommerce.core.security.jwt.enums.TokenType;
import com.build.ecommerce.core.security.jwt.token.JwtPayload;
import com.build.ecommerce.core.security.jwt.token.JwtProvider;
import com.build.ecommerce.userapi.helper.UnitTestHelper;
import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MvcResult;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class JwtTokenControllerTest extends UnitTestHelper {

    // 운영 키(user-api application-test.yml 의 service.jwt.key)와 다른 서명 키 (base64url, 32byte)
    private static final String OTHER_SIGNING_KEY = "llbCy_PRwTC-0PJH1jkf7iDgoAq73mI5TTOef6lFZ6Y";

    @Autowired
    private JwtProvider jwtProvider;

    @Test
    @DisplayName("RefreshToken 으로 토큰을 재발급한다")
    void getTokenRefresh() throws Exception {
        TokenRequest request = new TokenRequest(accessToken, refreshToken);

        mockMvc.perform(post("/client")
                        .headers(getHeaderSetting())
                        .content(objectMapper.writeValueAsString(request))
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.data.refreshToken").isNotEmpty());
    }

    @Test
    @DisplayName("재발급받은 RefreshToken 으로 다시 재발급할 수 있다")
    void getTokenRefreshChain() throws Exception {
        MvcResult result = mockMvc.perform(post("/client")
                        .headers(getHeaderSetting())
                        .content(objectMapper.writeValueAsString(new TokenRequest(accessToken, refreshToken)))
                ).andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        JsonNode data = objectMapper.readTree(result.getResponse().getContentAsString()).get("data");
        TokenRequest reissued = new TokenRequest(
                data.get("accessToken").asText(),
                data.get("refreshToken").asText()
        );

        mockMvc.perform(post("/client")
                        .headers(getHeaderSetting())
                        .content(objectMapper.writeValueAsString(reissued))
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.data.refreshToken").isNotEmpty());
    }

    @Test
    @DisplayName("RefreshToken 으로 인증이 필요한 API 를 호출하면 401 과 토큰 타입 불일치 메시지를 반환한다")
    void authenticateWithRefreshToken() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + refreshToken);

        mockMvc.perform(get("/v1/user").headers(headers))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value(ExceptionCode.TOKEN_TYPE_MISMATCH.getMessage()));
    }

    @Test
    @DisplayName("RefreshToken 자리에 AccessToken 을 전달하면 401 을 반환한다")
    void getTokenRefreshWithAccessToken() throws Exception {
        TokenRequest request = new TokenRequest(accessToken, accessToken);

        mockMvc.perform(post("/client")
                        .headers(getHeaderSetting())
                        .content(objectMapper.writeValueAsString(request))
                ).andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value(ExceptionCode.TOKEN_TYPE_MISMATCH.getMessage()));
    }

    @Test
    @DisplayName("형식이 잘못된 토큰을 전달하면 401 을 반환한다")
    void getTokenRefreshWithInvalidToken() throws Exception {
        TokenRequest request = new TokenRequest(accessToken, "not.a.jwt");

        mockMvc.perform(post("/client")
                        .headers(getHeaderSetting())
                        .content(objectMapper.writeValueAsString(request))
                ).andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value(ExceptionCode.AUTHENTICATION_UNAUTHORIZED.getMessage()));
    }

    @Test
    @DisplayName("만료된 RefreshToken 으로 재발급을 요청하면 401 과 만료 메시지를 반환한다")
    void getTokenRefreshWithExpiredRefreshToken() throws Exception {
        TokenRequest request = new TokenRequest(accessToken, expiredToken(TokenType.REFRESH));

        mockMvc.perform(post("/client")
                        .headers(getHeaderSetting())
                        .content(objectMapper.writeValueAsString(request))
                ).andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value(ExceptionCode.TOKEN_EXPIRED.getMessage()));
    }

    @Test
    @DisplayName("만료된 AccessToken 으로 인증이 필요한 API 를 호출하면 401 과 만료 메시지를 반환한다")
    void authenticateWithExpiredAccessToken() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + expiredToken(TokenType.ACCESS));

        mockMvc.perform(get("/v1/user").headers(headers))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value(ExceptionCode.TOKEN_EXPIRED.getMessage()));
    }

    @Test
    @DisplayName("다른 키로 서명된 토큰을 전달하면 401 을 반환한다")
    void getTokenRefreshWithTokenSignedByOtherKey() throws Exception {
        TokenRequest request = new TokenRequest(accessToken, forgedRefreshToken());

        mockMvc.perform(post("/client")
                        .headers(getHeaderSetting())
                        .content(objectMapper.writeValueAsString(request))
                ).andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value(ExceptionCode.AUTHENTICATION_UNAUTHORIZED.getMessage()));
    }

    @Test
    @DisplayName("형식이 잘못된 AccessToken은 401과 기존 인증 실패 메시지를 반환한다")
    void authenticateWithInvalidAccessToken() throws Exception {
        mockMvc.perform(get("/v1/user")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer not.a.jwt"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value(ExceptionCode.AUTHENTICATION_UNAUTHORIZED.getMessage()));
    }

    private String expiredToken(TokenType tokenType) {
        return jwtProvider.createToken(
                JwtPayload.builder()
                        .tokenType(tokenType)
                        .id(1L)
                        .authority("USER")
                        .issuedAt(new Date())
                        .build(),
                -1000L
        );
    }

    /**
     * 운영 키가 아닌 다른 키로 서명한 위조 RefreshToken 을 만든다.
     * jjwt 라이브러리가 user-api 테스트 클래스패스에 노출돼 있지 않아
     * 표준 JCA(Mac/SecretKeySpec)로 JWT(HS256) 를 직접 구성한다.
     */
    private String forgedRefreshToken() throws Exception {
        long nowSeconds = System.currentTimeMillis() / 1000;
        String header = "{\"alg\":\"HS256\"}";
        String payload = String.format(
                "{\"id\":\"1\",\"authority\":\"USER\",\"tokenType\":\"REFRESH\",\"iat\":%d,\"exp\":%d}",
                nowSeconds, nowSeconds + 60
        );

        String headerPart = Base64.getUrlEncoder().withoutPadding().encodeToString(header.getBytes(StandardCharsets.UTF_8));
        String payloadPart = Base64.getUrlEncoder().withoutPadding().encodeToString(payload.getBytes(StandardCharsets.UTF_8));
        String signingInput = headerPart + "." + payloadPart;

        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(Base64.getUrlDecoder().decode(OTHER_SIGNING_KEY), "HmacSHA256"));
        String signaturePart = Base64.getUrlEncoder().withoutPadding().encodeToString(mac.doFinal(signingInput.getBytes(StandardCharsets.UTF_8)));

        return signingInput + "." + signaturePart;
    }
}
