package com.riya.aichatbot.ai;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OllamaServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private OllamaService ollamaService;

    @Test
    void testChatMethodExists() {
        // Set the required fields using reflection
        try {
            java.lang.reflect.Field baseUrlField = OllamaService.class.getDeclaredField("baseUrl");
            baseUrlField.setAccessible(true);
            baseUrlField.set(ollamaService, "http://localhost:11434");

            java.lang.reflect.Field modelField = OllamaService.class.getDeclaredField("model");
            modelField.setAccessible(true);
            modelField.set(ollamaService, "llama3.2");
        } catch (Exception e) {
            fail("Failed to set private fields: " + e.getMessage());
        }

        List<Map<String, String>> history = List.of(
                Map.of("role", "user", "content", "Hello")
        );

        // Mock the RestTemplate response
        Map<String, Object> mockResponse = Map.of(
                "message", Map.of("content", "Hi there!")
        );
        when(restTemplate.postForObject(any(String.class), any(), eq(Map.class)))
                .thenReturn(mockResponse);

        assertDoesNotThrow(() -> {
            String result = ollamaService.chat(history);
            assertEquals("Hi there!", result);
        });
    }
}