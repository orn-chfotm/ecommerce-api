package com.build.ecommerce.adminapi.helper;

import com.build.ecommerce.domain.admin.dto.request.AdminRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UnitTestHelper {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    protected static String adminAccessToken;

    @BeforeAll
    @DisplayName("관리자 생성 및 Token 발급")
    public void createAdmin() throws Exception {
        if (adminAccessToken != null) {
            return;
        }

        String email = "admin@email.com";
        String password = "testPassword";
        AdminRequest adminRequest = new AdminRequest(
                email,
                password,
                "testAdmin",
                "ADMIN"
        );

        mockMvc.perform(post("/v1/admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(adminRequest))
                )
                .andDo(print());

        MvcResult result = mockMvc.perform(post("/v1/login/admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginDto(email, password)))
                )
                .andDo(print())
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(jsonResponse);
        JsonNode data = jsonNode.get("data");
        adminAccessToken = data.get("accessToken").asText();
    }

    protected HttpHeaders getHeaderSetting() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        return headers;
    }

    protected HttpHeaders getAdminAccessToken() {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + adminAccessToken);
        return headers;
    }

    private static class LoginDto {
        private String email;
        private String password;

        public String getEmail() { return email; }
        public String getPassword() { return password; }

        public LoginDto(String email, String password) {
            this.email = email;
            this.password = password;
        }
    }
}
