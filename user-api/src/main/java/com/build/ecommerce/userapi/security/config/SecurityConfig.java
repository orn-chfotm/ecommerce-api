package com.build.ecommerce.userapi.security.config;

import com.build.ecommerce.core.security.jwt.auth.JwtAuthenticationProvider;
import com.build.ecommerce.core.security.jwt.token.JwtService;
import com.build.ecommerce.userapi.security.login.CustomUserDetailService;
import com.build.ecommerce.userapi.security.login.CustomUserLoginProvider;
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

    private final CustomUserDetailService userDetailsService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    @Bean
    AuthenticationManager providerManager() {
        return new ProviderManager(List.of(
                jwtAuthenticationProvider(),
                customUserLoginProvider()
        ));
    }

    AuthenticationProvider jwtAuthenticationProvider() {
        return new JwtAuthenticationProvider(jwtService);
    }

    AuthenticationProvider customUserLoginProvider() {
        return new CustomUserLoginProvider(
                passwordEncoder,
                userDetailsService
        );
    }
}
