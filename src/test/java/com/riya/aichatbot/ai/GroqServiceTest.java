
package com.riya.aichatbot.ai;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.riya.aichatbot.ai.service.GroqService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class GroqServiceTest {

    private GroqService groqService;
    private MockRestServiceServer mockServer;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder();

        mockServer = MockRestServiceServer.bindTo(builder).build();

        groqService = new GroqService(builder, new ObjectMapper());

        ReflectionTestUtils.setField(
                groqService, "baseUrl", "https://api.groq.test/openai/v1");

        ReflectionTestUtils.setField(
                groqService, "model", "test-model");

        ReflectionTestUtils.setField(
                groqService, "apiKey", "test-api-key");
    }

    @Test
    void testChatReturnsAssistantResponse() {

        List<Map<String, String>> history = List.of(
                Map.of("role", "user", "content", "Hello"));

        String responseJson = """
                {
                    "choices": [
                        {
                            "message": {
                                "content": "Hi there!"
                            }
                        }
                    ]
                }
                """;

        mockServer.expect(
                requestTo("https://api.groq.test/openai/v1/chat/completions"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header(
                        "Authorization", "Bearer test-api-key"))
                .andRespond(withSuccess(
                        responseJson, MediaType.APPLICATION_JSON));

        String result = groqService.chat(history);

        assertEquals("Hi there!", result);

        mockServer.verify();
    }
}
