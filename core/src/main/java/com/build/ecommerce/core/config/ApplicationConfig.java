package com.build.ecommerce.core.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {

    @Bean
    ObjectMapper objectMapper() {
        // Boot 자동구성 ObjectMapper를 대체하는 커스텀 빈이므로 JavaTimeModule을 직접 등록한다.
        // 등록하지 않으면 java.time.* 타입(LocalDateTime 등)이 응답에 포함될 때 500(HttpMessageConversionException)이 발생한다.
        return new ObjectMapper().registerModule(new JavaTimeModule());
    }
}
