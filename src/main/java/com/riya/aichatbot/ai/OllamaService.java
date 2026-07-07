package com.riya.aichatbot.ai;

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
public class OllamaService {

    @Value("${ollama.base-url}")
    private String baseUrl;

    @Value("${ollama.model}")
    private String model;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public OllamaService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    public void printConfig() {
        System.out.println("====================================");
        System.out.println("Ollama Base URL = " + baseUrl);
        System.out.println("Ollama Model = " + model);
        System.out.println("====================================");
    }

    public String chat(List<Map<String, String>> messageHistory) {
    String url = baseUrl + "/api/chat";

    try {
        String jsonBody = objectMapper.writeValueAsString(Map.of(
                "model", model.trim(),
                "messages", messageHistory,
                "stream", false
        ));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> request = new HttpEntity<>(jsonBody, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(
                url,
                request,
                String.class
        );

        JsonNode root = objectMapper.readTree(response.getBody());
        JsonNode contentNode = root.path("message").path("content");

        if (contentNode.isMissingNode() || contentNode.asText().isBlank()) {
            throw new RuntimeException("Ollama returned no assistant message");
        }

        return contentNode.asText();

    } catch (Exception e) {
        throw new RuntimeException(
                "Ollama request failed. URL=" + url
                        + ", model=" + model.trim()
                        + ", response=" + e.getMessage(),
                e
        );
    }
}
}