package com.build.ecommerce.adminapi;

import com.build.ecommerce.adminapi.config.TestcontainersConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestcontainersConfig.class)
class AdminApiApplicationTest {

    @Test
    void contextLoads() {
    }
}
