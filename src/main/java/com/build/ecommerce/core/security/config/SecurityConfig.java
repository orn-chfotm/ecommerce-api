package com.build.ecommerce.core.security.config;

import com.build.ecommerce.core.jwt.service.JwtService;
import com.build.ecommerce.core.security.jwt.JwtAuthenticationProvider;
import com.build.ecommerce.core.security.login.admin.CustomAdminDetailService;
import com.build.ecommerce.core.security.login.admin.CustomAdminLoginProvider;
import com.build.ecommerce.core.security.login.user.CustomUserDetailService;
import com.build.ecommerce.core.security.login.user.CustomUserLoginProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailService userDetailsService;
    private final CustomAdminDetailService adminDetailService;
    private final JwtService jwtService;

    @Bean
    PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    AuthenticationManager providerManager() {
        return new ProviderManager(List.of(
                jwtAuthenticationProvider(),
                customUserLoginProvider(),
                customAdminLoginProvider()
        ));
    }

    AuthenticationProvider jwtAuthenticationProvider() {
        return new JwtAuthenticationProvider(jwtService);
    }

    AuthenticationProvider customUserLoginProvider() {
        return new CustomUserLoginProvider(
                passwordEncoder(),
                userDetailsService
        );
    }

    AuthenticationProvider customAdminLoginProvider() {
        return new CustomAdminLoginProvider(
                passwordEncoder(),
                adminDetailService
        );
    }
}
