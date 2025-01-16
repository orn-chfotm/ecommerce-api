package com.build.ecommerce.core.config;

import com.build.ecommerce.core.security.handler.CustomAccessDeniedHandler;
import com.build.ecommerce.core.security.handler.CustomAuthenticationEntryPoint;
import com.build.ecommerce.core.security.handler.login.LoginFailureHandler;
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
    CustomAuthenticationEntryPoint customAuthenticationEntryPoint() {
        return new CustomAuthenticationEntryPoint();
    }

    @Bean
    CustomAccessDeniedHandler customAccessDeniedHandler() {
        return new CustomAccessDeniedHandler();
    }

}
