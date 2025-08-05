package com.build.ecommerce.core.security.login.common.handler;

import com.build.ecommerce.core.jwt.JwtPayload;
import com.build.ecommerce.core.jwt.dto.response.TokenResponse;
import com.build.ecommerce.core.jwt.service.JwtService;
import com.build.ecommerce.core.security.login.common.token.CustomLoginToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@RequiredArgsConstructor
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtService jwtService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        CustomLoginToken customToken = (CustomLoginToken) authentication;

        TokenResponse tokenResponse = jwtService.createToken(
                new JwtPayload(customToken.getId(), customToken.getRole(), new Date())
        );

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        new ObjectMapper().writeValue(response.getWriter(), tokenResponse);

    }
}
