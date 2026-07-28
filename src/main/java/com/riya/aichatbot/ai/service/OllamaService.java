package com.riya.aichatbot.ai.service;

import com.riya.aichatbot.ai.dto.EmbeddingRequest;
import com.riya.aichatbot.ai.dto.EmbeddingResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
public class OllamaService {

    private static final Logger logger =
            LoggerFactory.getLogger(OllamaService.class);

    private final RestClient restClient;

    @Value("${ollama.base-url}")
    private String baseUrl;

    @Value("${ollama.embedding-model}")
    private String embeddingModel;

    public OllamaService(RestClient restClient) {
        this.restClient = restClient;
    }

    public List<Double> generateEmbedding(String text) {

        logger.info("Generating embedding...");

        EmbeddingRequest request =
                new EmbeddingRequest(
                        embeddingModel,
                        text
                );

        EmbeddingResponse response =
                restClient.post()
                        .uri(baseUrl + "/api/embeddings")
                        .body(request)
                        .retrieve()
                        .body(EmbeddingResponse.class);

        if (response == null || response.embedding() == null) {
            throw new RuntimeException("Failed to generate embedding from Ollama.");
        }

        logger.info("Embedding generated successfully.");

        return response.embedding();
    }

}