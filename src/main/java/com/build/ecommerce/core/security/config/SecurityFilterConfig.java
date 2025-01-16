package com.build.ecommerce.core.security.config;

import com.build.ecommerce.core.jwt.JwtProvider;
import com.build.ecommerce.core.jwt.property.JwtProperty;
import com.build.ecommerce.core.jwt.service.JwtService;
import com.build.ecommerce.core.security.handler.CustomAccessDeniedHandler;
import com.build.ecommerce.core.security.handler.CustomAuthenticationEntryPoint;
import com.build.ecommerce.core.security.jwt.JwtAuthenticationFilter;
import com.build.ecommerce.core.security.jwt.JwtAuthenticationProvider;
import com.build.ecommerce.core.security.login.admin.CustomAdminLoginFilter;
import com.build.ecommerce.core.security.login.admin.CustomAdminLoginProvider;
import com.build.ecommerce.core.security.login.handler.LoginFailureHandler;
import com.build.ecommerce.core.security.login.handler.LoginSuccessHandler;
import com.build.ecommerce.core.security.login.user.CustomUserLoginFilter;
import com.build.ecommerce.core.security.login.user.CustomUserLoginProvider;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityFilterConfig {

    private final CustomAuthenticationEntryPoint authenticationEntryPoint;
    private final CustomAccessDeniedHandler accessDeniedHandler;
    private final JwtProperty jwtProperty;
    private final JwtService jwtService;
    private final Validator validator;
    private final AuthenticationManager authenticationManager;

    @Bean
    SecurityFilterChain http(HttpSecurity http) throws Exception {
        http
            .httpBasic(AbstractHttpConfigurer::disable)
            .formLogin(AbstractHttpConfigurer::disable)
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(sessionConfig ->
                    sessionConfig.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            );

        http
                .authorizeHttpRequests(auth -> {
                    auth
                            .requestMatchers(HttpMethod.POST, "/v1/admin", "/v1/user", "/v1/login/**").permitAll()
                            .requestMatchers("/h2-console/**", "/swagger-ui/**", "/swagger/**", "/swagger-resources/**", "/v3/**").permitAll()
                            .anyRequest().authenticated();
                })
                .headers(header -> {
                    header.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable);
                })
                .exceptionHandling(exceptionConfigurer -> {
                    exceptionConfigurer.authenticationEntryPoint(authenticationEntryPoint);
                    exceptionConfigurer.accessDeniedHandler(accessDeniedHandler);
                })
        ;

        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
        http.addFilterAfter(adminLoginFilter(), JwtAuthenticationFilter.class);
        http.addFilterAfter(userLoginFilter(), JwtAuthenticationFilter.class);

        return http.build();
    }



    @Bean
    AuthenticationSuccessHandler loginSuccessHandler() {
        return new LoginSuccessHandler(jwtService);
    }

    @Bean
    AuthenticationFailureHandler loginFailureHandler() {
        return new LoginFailureHandler();
    }

    @Bean
    AbstractAuthenticationProcessingFilter adminLoginFilter() throws Exception {
        AbstractAuthenticationProcessingFilter customAdminLoginFilter = new CustomAdminLoginFilter(validator);
        customAdminLoginFilter.setAuthenticationManager(authenticationManager);
        customAdminLoginFilter.setAuthenticationFailureHandler(loginFailureHandler());
        customAdminLoginFilter.setAuthenticationSuccessHandler(loginSuccessHandler());
        return customAdminLoginFilter;
    }

    @Bean
    AbstractAuthenticationProcessingFilter userLoginFilter() throws Exception {
        AbstractAuthenticationProcessingFilter customUserLoginFilter = new CustomUserLoginFilter(validator);
        customUserLoginFilter.setAuthenticationManager(authenticationManager);
        customUserLoginFilter.setAuthenticationFailureHandler(loginFailureHandler());
        customUserLoginFilter.setAuthenticationSuccessHandler(loginSuccessHandler());
        return customUserLoginFilter;
    }

    @Bean
    JwtAuthenticationFilter jwtAuthenticationFilter() throws Exception {
        return new JwtAuthenticationFilter(authenticationManager, jwtProperty);
    }
}
