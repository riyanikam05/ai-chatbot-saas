package com.riya.aichatbot.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
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

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private JsonNode mockRoot;

    @Mock
    private JsonNode mockMessageNode;

    @Mock
    private JsonNode mockContentNode;

    @InjectMocks
    private OllamaService ollamaService;

    @Test
    void testChatMethodExists() throws Exception {
        // Set the required fields using reflection
        java.lang.reflect.Field baseUrlField = OllamaService.class.getDeclaredField("baseUrl");
        baseUrlField.setAccessible(true);
        baseUrlField.set(ollamaService, "http://localhost:11434");

        java.lang.reflect.Field modelField = OllamaService.class.getDeclaredField("model");
        modelField.setAccessible(true);
        modelField.set(ollamaService, "qwen2.5:1.5b");

        List<Map<String, String>> history = List.of(
                Map.of("role", "user", "content", "Hello")
        );

        // Mock the RestTemplate response
        String mockResponseJson = "{\"message\":{\"content\":\"Hi there!\"}}";
        ResponseEntity<String> mockResponse = ResponseEntity.ok(mockResponseJson);
        
        when(restTemplate.postForEntity(any(String.class), any(HttpEntity.class), eq(String.class)))
                .thenReturn(mockResponse);

        // Mock ObjectMapper
        when(objectMapper.readTree(any(String.class))).thenReturn(mockRoot);
        when(mockRoot.path("message")).thenReturn(mockMessageNode);
        when(mockMessageNode.path("content")).thenReturn(mockContentNode);
        when(mockContentNode.isMissingNode()).thenReturn(false);
        when(mockContentNode.asText()).thenReturn("Hi there!");

        String result = ollamaService.chat(history);
        assertEquals("Hi there!", result);
    }
}