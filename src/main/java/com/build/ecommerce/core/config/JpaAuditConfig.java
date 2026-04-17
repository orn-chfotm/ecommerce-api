package com.build.ecommerce.core.config;

import com.build.ecommerce.core.persistence.SecurityAuditorAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;

@Configuration
public class JpaAuditConfig {

    @Bean
    public AuditorAware<String> auditorProvider() {
        return new SecurityAuditorAware();
    }
}
