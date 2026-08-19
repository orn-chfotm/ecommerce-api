package com.build.ecommerce.adminapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaAuditing
@ConfigurationPropertiesScan(basePackages = {"com.build.ecommerce.core", "com.build.ecommerce.adminapi"})
@SpringBootApplication(scanBasePackages = {
        "com.build.ecommerce.core",
        "com.build.ecommerce.domain",
        "com.build.ecommerce.infra",
        "com.build.ecommerce.adminapi"
})
@EntityScan(basePackages = {"com.build.ecommerce.domain", "com.build.ecommerce.infra"})
@EnableJpaRepositories(basePackages = "com.build.ecommerce.infra.persistence")
public class AdminApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(AdminApiApplication.class, args);
    }
}
