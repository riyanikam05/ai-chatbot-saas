
package com.riya.aichatbot.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.riya.aichatbot.auth.dto.LoginRequest;
import com.riya.aichatbot.auth.dto.RegisterRequest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        private String uniqueEmail() {
                return "test-" + UUID.randomUUID() + "@example.com";
        }

        private RegisterRequest createRegisterRequest(String email) {
                RegisterRequest request = new RegisterRequest();
                request.setName("Test User");
                request.setEmail(email);
                request.setPassword("password123");
                return request;
        }

        private void registerUser(String email) throws Exception {
                mockMvc.perform(post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(
                                                createRegisterRequest(email))))
                                .andExpect(status().isOk());
        }

        @Test
        void testRegister() throws Exception {

                String email = uniqueEmail();

                mockMvc.perform(post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(
                                                createRegisterRequest(email))))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.message")
                                                .value("Email registered successfully"));
        }

        @Test
        void testRegisterDuplicateEmail() throws Exception {

                String email = uniqueEmail();

                registerUser(email);

                mockMvc.perform(post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(
                                                createRegisterRequest(email))))
                                .andExpect(status().isConflict());
        }

        @Test
        void testLogin() throws Exception {

                String email = uniqueEmail();

                registerUser(email);

                LoginRequest loginRequest = new LoginRequest();
                loginRequest.setEmail(email);
                loginRequest.setPassword("password123");

                mockMvc.perform(post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(loginRequest)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.token").exists())
                                .andExpect(jsonPath("$.userId").exists())
                                .andExpect(jsonPath("$.name").value("Test User"))
                                .andExpect(jsonPath("$.email").value(email));
        }

        @Test
        void testLoginWrongPassword() throws Exception {

                String email = uniqueEmail();

                registerUser(email);

                LoginRequest loginRequest = new LoginRequest();
                loginRequest.setEmail(email);
                loginRequest.setPassword("wrongpassword");

                mockMvc.perform(post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(loginRequest)))
                                .andExpect(status().isUnauthorized())
                                .andExpect(jsonPath("$.error").exists());
        }
}
