package com.build.ecommerce.helper;

import com.build.ecommerce.domain.user.dto.request.UserRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
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

    protected static String accessToken;
    protected static String refreshToken;

    @BeforeAll
    @DisplayName("유저 생성 및 Token 발급")
    public void createUser() throws Exception {
        if (accessToken != null) {
            return;
        }

        String email = "test@email.com";
        String password = "testPassword";
        UserRequest userRequest = new UserRequest(
                email,
                password,
                "testUser",
                "M",
                "2024-01-08"
        );

        mockMvc.perform(post("/v1/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequest))
                )
                .andDo(print());

        MvcResult result = mockMvc.perform(post("/v1/login/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginDto(email, password)))
                )
                .andDo(print())
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(jsonResponse);
        JsonNode data = jsonNode.get("data");
        accessToken = data.get("accessToken").asText();
        refreshToken = data.get("refreshToken").asText();
    }

    protected HttpHeaders getHeaderSetting() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        return headers;
    }

    protected HttpHeaders getAccessToken() {
        String sendAccessToken = "Bearer " + accessToken;

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, sendAccessToken);

        return headers;
    }

    private static class LoginDto{
        private String email;
        private String password;

        public String getEmail() {
            return email;
        }

        public String getPassword() {
            return password;
        }

        public LoginDto(String email, String password) {
            this.email = email;
            this.password = password;
        }
    }
}
