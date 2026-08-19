package com.build.ecommerce.userapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaAuditing
@ConfigurationPropertiesScan(basePackages = {"com.build.ecommerce.core", "com.build.ecommerce.userapi"})
@SpringBootApplication(scanBasePackages = {
        "com.build.ecommerce.core",
        "com.build.ecommerce.domain",
        "com.build.ecommerce.infra",
        "com.build.ecommerce.userapi"
})
@EntityScan(basePackages = {"com.build.ecommerce.domain", "com.build.ecommerce.infra"})
@EnableJpaRepositories(basePackages = "com.build.ecommerce.infra.persistence")
public class UserApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserApiApplication.class, args);
    }
}
