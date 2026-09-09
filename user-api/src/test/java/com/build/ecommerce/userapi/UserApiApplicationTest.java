package com.build.ecommerce.userapi;

import com.build.ecommerce.userapi.config.TestcontainersConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestcontainersConfig.class)
class UserApiApplicationTest {

    @Test
    void contextLoads() {
    }
}
