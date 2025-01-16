package com.build.ecommerce.core.security.handler.login;

import com.build.ecommerce.core.jwt.JwtPayload;
import com.build.ecommerce.core.security.dto.response.TokenResponse;
import com.build.ecommerce.core.jwt.service.JwtService;
import com.build.ecommerce.core.security.auth.token.login.CustomLoginToken;
import com.build.ecommerce.core.security.util.HandlerUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;
import java.util.Date;

@RequiredArgsConstructor
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtService jwtService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        HandlerUtil.toResponse(response);

        CustomLoginToken customToken = (CustomLoginToken) authentication;
        TokenResponse tokenResponse = jwtService.createToken(
                new JwtPayload(customToken.getId(), customToken.getRole(), new Date())
        );
        new ObjectMapper().writeValue(response.getWriter(), tokenResponse);

    }
}
