package com.build.ecommerce.adminapi.security.config;

import com.build.ecommerce.adminapi.security.login.CustomAdminDetailService;
import com.build.ecommerce.adminapi.security.login.CustomAdminLoginProvider;
import com.build.ecommerce.core.security.jwt.auth.JwtAuthenticationProvider;
import com.build.ecommerce.core.security.jwt.token.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomAdminDetailService adminDetailService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    @Bean
    AuthenticationManager providerManager() {
        return new ProviderManager(List.of(
                jwtAuthenticationProvider(),
                customAdminLoginProvider()
        ));
    }

    AuthenticationProvider jwtAuthenticationProvider() {
        return new JwtAuthenticationProvider(jwtService);
    }

    AuthenticationProvider customAdminLoginProvider() {
        return new CustomAdminLoginProvider(
                passwordEncoder,
                adminDetailService
        );
    }
}
