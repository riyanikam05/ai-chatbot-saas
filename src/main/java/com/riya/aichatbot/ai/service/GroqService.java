package com.riya.aichatbot.ai.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.riya.aichatbot.common.constants.AppConstants;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

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

    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    public GroqService(RestClient.Builder builder,
            ObjectMapper objectMapper) {

        this.restClient = builder.build();
        this.objectMapper = objectMapper;
    }

    public String chat(List<Map<String, String>> messageHistory) {
        System.out.println("Groq model = " + model);

        String url = baseUrl + "/chat/completions";

        try {

            String jsonBody = objectMapper.writeValueAsString(
                    Map.of(
                            "model", model,
                            "messages", messageHistory,
                            "temperature", AppConstants.DEFAULT_TEMPERATURE,
                            "max_completion_tokens", AppConstants.DEFAULT_MAX_TOKENS,
                            "stream", false));

            String response = restClient.post()
                    .uri(url)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + apiKey)
                    .body(jsonBody)
                    .retrieve()
                    .body(String.class);

            JsonNode root = objectMapper.readTree(response);

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
                    "Groq request failed. model="
                            + model
                            + ", response="
                            + e.getMessage(),
                    e);
        }
    }
}