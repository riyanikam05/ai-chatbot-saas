package com.riya.aichatbot.ai.service;

import com.riya.aichatbot.ai.dto.EmbeddingResponse;
import com.riya.aichatbot.ai.dto.OllamaEmbeddingRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import com.riya.aichatbot.exception.EmbeddingGenerationException;
import org.springframework.web.client.RestClientException;

import java.util.List;

@Service
public class OllamaService {

    private static final Logger logger = LoggerFactory.getLogger(OllamaService.class);

    private final RestClient restClient;

    @Value("${ollama.base-url}")
    private String baseUrl;

    @Value("${ollama.embedding-model}")
    private String model;

    public OllamaService(RestClient.Builder builder) {
        this.restClient = builder.build();
    }

    public List<Double> generateEmbedding(String text) {

        logger.info("Generating embedding...");

        try {

            OllamaEmbeddingRequest request = new OllamaEmbeddingRequest(model, text);

            EmbeddingResponse response = restClient.post()
                    .uri(baseUrl + "/api/embed")
                    .body(request)
                    .retrieve()
                    .body(EmbeddingResponse.class);

            if (response == null
                    || response.embeddings() == null
                    || response.embeddings().isEmpty()) {

                throw new EmbeddingGenerationException(
                        "Ollama returned an empty embedding.");
            }

            return response.embeddings().getFirst();

        } catch (RestClientException ex) {

            logger.error("Failed to call Ollama.", ex);

            throw new EmbeddingGenerationException(
                    "Failed to connect to Ollama.",
                    ex);

        } catch (Exception ex) {

            logger.error("Embedding generation failed.", ex);

            throw new EmbeddingGenerationException(
                    "Failed to generate embedding.",
                    ex);
        }
    }
}