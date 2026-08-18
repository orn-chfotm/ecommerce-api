package com.build.ecommerce.core.security.login.common.handler;

import com.build.ecommerce.core.response.SuccessResponse;
import com.build.ecommerce.core.security.jwt.dto.response.TokenResponse;
import com.build.ecommerce.core.security.jwt.enums.TokenType;
import com.build.ecommerce.core.security.jwt.token.JwtPayload;
import com.build.ecommerce.core.security.jwt.token.JwtService;
import com.build.ecommerce.core.security.login.common.token.CustomLoginToken;
import com.build.ecommerce.core.support.servlet.CustomHandlerUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;
import java.util.Date;

@RequiredArgsConstructor
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        CustomLoginToken customToken = (CustomLoginToken) authentication;
        TokenResponse tokenResponse = jwtService.createToken(
                new JwtPayload(TokenType.ACCESS, customToken.getId(), customToken.getRole(), new Date())
        );

        CustomHandlerUtil.toResponse(response, HttpStatus.OK);
        objectMapper.writeValue(response.getWriter(), SuccessResponse.toResponse(tokenResponse).getBody());
    }
}
