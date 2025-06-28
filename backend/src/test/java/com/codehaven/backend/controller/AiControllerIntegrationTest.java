package com.codehaven.backend.controller;

import com.codehaven.backend.application.dto.ai.CodeReviewRequest;
import com.codehaven.backend.application.dto.ai.BugFixRequest;
import com.codehaven.backend.domain.model.User;
import com.codehaven.backend.domain.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
    "app.groq.api-key=test-api-key",
    "app.groq.base-url=https://api.groq.com/openai/v1",
    "app.groq.model=llama3-8b-8192"
})
@Transactional
public class AiControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        // MockMvc is already configured with @AutoConfigureMockMvc
    }

    @Test
    @WithMockUser(username = "testuser")
    void testCodeReviewEndpoint() throws Exception {
        // Create a test user
        User testUser = User.builder()
                .username("testuser")
                .email("test@example.com")
                .password("password")
                .role(User.Role.USER)
                .build();
        userRepository.save(testUser);

        CodeReviewRequest request = new CodeReviewRequest();
        request.setCode("public class Test { private String name; }");
        request.setLanguage("java");

        mockMvc.perform(post("/api/ai/code-review")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @WithMockUser(username = "testuser")
    void testBugFixEndpoint() throws Exception {
        // Create a test user
        User testUser = User.builder()
                .username("testuser")
                .email("test@example.com")
                .password("password")
                .role(User.Role.USER)
                .build();
        userRepository.save(testUser);

        BugFixRequest request = new BugFixRequest();
        request.setCode("public class Test { private String name; public void setName(String name) { this.name = name; } }");
        request.setLanguage("java");
        request.setErrorDescription("NullPointerException when name is null");

        mockMvc.perform(post("/api/ai/bug-fix")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @WithMockUser(username = "testuser")
    void testGetHistoryEndpoint() throws Exception {
        // Create a test user
        User testUser = User.builder()
                .username("testuser")
                .email("test@example.com")
                .password("password")
                .role(User.Role.USER)
                .build();
        userRepository.save(testUser);

        mockMvc.perform(get("/api/ai/history")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").isArray());
    }

    @Test
    void testUnauthorizedAccess() throws Exception {
        CodeReviewRequest request = new CodeReviewRequest();
        request.setCode("public class Test { }");
        request.setLanguage("java");

        mockMvc.perform(post("/api/ai/code-review")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }
}
