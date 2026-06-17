package com.build.ecommerce.core.security.config;

import com.build.ecommerce.core.security.handler.CustomAccessDeniedHandler;
import com.build.ecommerce.core.security.handler.CustomAuthenticationEntryPoint;
import com.build.ecommerce.core.security.login.common.handler.LoginFailureHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class SecurityHandleConfig {

    @Bean
    LoginFailureHandler customAuthenticationFailureHandler() {
        return new LoginFailureHandler();
    }

    @Bean
    CustomAuthenticationEntryPoint customAuthenticationEntryPoint(ObjectMapper objectMapper) {
        return new CustomAuthenticationEntryPoint(objectMapper);
    }

    @Bean
    CustomAccessDeniedHandler customAccessDeniedHandler(ObjectMapper objectMapper) {
        return new CustomAccessDeniedHandler(objectMapper);
    }

}
