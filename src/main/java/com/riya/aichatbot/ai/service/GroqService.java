package com.riya.aichatbot.ai.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class GroqService {

    @Value("${groq.base-url}")
    private String baseUrl;

    @Value("${groq.model}")
    private String model;

    @Value("${groq.api-key}")
    private String apiKey;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public GroqService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }


    public String chat(List<Map<String, String>> messageHistory) {
        String url = baseUrl + "/chat/completions";

        try {
            String jsonBody = objectMapper.writeValueAsString(Map.of(
                    "model", model,
                    "messages", messageHistory,
                    "temperature", 0.5,
                    "max_completion_tokens", 150,
                    "stream", false
            ));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            HttpEntity<String> request = new HttpEntity<>(jsonBody, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(
                    url,
                    request,
                    String.class
            );

            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode contentNode = root.path("choices")
                    .path(0)
                    .path("message")
                    .path("content");

            if (contentNode.isMissingNode() || contentNode.asText().isBlank()) {
                throw new RuntimeException("Groq returned no assistant message");
            }

            return contentNode.asText();

        } catch (Exception e) {
            throw new RuntimeException(
                    "Groq request failed. model=" + model
                            + ", response=" + e.getMessage(),
                    e
            );
        }
    }
}