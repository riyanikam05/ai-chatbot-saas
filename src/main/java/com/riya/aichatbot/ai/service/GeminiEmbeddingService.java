
package com.riya.aichatbot.ai.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.riya.aichatbot.exception.EmbeddingGenerationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

import java.util.List;
import java.util.Map;

@Service
public class GeminiEmbeddingService {

        private static final Logger logger = LoggerFactory.getLogger(GeminiEmbeddingService.class);

        private static final String BASE_URL = "https://generativelanguage.googleapis.com/v1beta";

        private static final int EXPECTED_DIMENSIONS = 768;

        private final RestClient restClient;
        private final String apiKey;
        private final String model;
        private final int dimensions;

        public GeminiEmbeddingService(
                        RestClient.Builder builder,
                        @Value("${gemini.api-key}") String apiKey,
                        @Value("${gemini.embedding-model}") String model,
                        @Value("${gemini.embedding-dimensions:768}") int dimensions) {

                if (dimensions != EXPECTED_DIMENSIONS) {
                        throw new IllegalArgumentException(
                                        "Gemini embedding dimensions must match PostgreSQL VECTOR(768).");
                }

                this.restClient = builder
                                .baseUrl(BASE_URL)
                                .build();

                this.apiKey = apiKey;
                this.model = model;
                this.dimensions = dimensions;
        }

        public List<Double> generateDocumentEmbedding(String text) {
                return generateEmbedding(text, "RETRIEVAL_DOCUMENT");
        }

        public List<Double> generateQueryEmbedding(String text) {
                return generateEmbedding(text, "RETRIEVAL_QUERY");
        }

        private List<Double> generateEmbedding(String text, String taskType) {

                if (text == null || text.isBlank()) {
                        throw new EmbeddingGenerationException(
                                        "Cannot generate an embedding for empty text.");
                }

                if (apiKey == null || apiKey.isBlank()) {
                        throw new EmbeddingGenerationException(
                                        "Gemini API key is not configured.");
                }

                Map<String, Object> request = Map.of(
                                "content", Map.of(
                                                "parts", List.of(
                                                                Map.of("text", text))),
                                "taskType", taskType,
                                "outputDimensionality", dimensions);

                try {
                        EmbeddingApiResponse response = restClient.post()
                                        .uri("/models/{model}:embedContent", model)
                                        .header("x-goog-api-key", apiKey)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .body(request)
                                        .retrieve()
                                        .body(EmbeddingApiResponse.class);

                        if (response == null
                                        || response.embedding() == null
                                        || response.embedding().values() == null) {

                                throw new EmbeddingGenerationException(
                                                "Gemini returned an empty embedding response.");
                        }

                        List<Double> values = response.embedding().values();

                        if (values.size() != dimensions) {
                                throw new EmbeddingGenerationException(
                                                "Expected " + dimensions
                                                                + " embedding dimensions but received "
                                                                + values.size() + ".");
                        }

                        // Validate every embedding value before normalization.
                        if (values.stream().anyMatch(
                                        value -> value == null || !Double.isFinite(value))) {

                                throw new EmbeddingGenerationException(
                                                "Gemini returned invalid embedding values.");
                        }

                        // Calculate the L2 norm.
                        double norm = Math.sqrt(
                                        values.stream()
                                                        .mapToDouble(value -> value * value)
                                                        .sum());

                        if (norm == 0.0 || !Double.isFinite(norm)) {
                                throw new EmbeddingGenerationException(
                                                "Gemini returned an invalid embedding vector.");
                        }

                        // Normalize the vector to unit length.
                        return values.stream()
                                        .map(value -> value / norm)
                                        .toList();

                } catch (RestClientResponseException ex) {

                        logger.error(
                                        "Gemini API returned HTTP {}. Response: {}",
                                        ex.getStatusCode().value(),
                                        ex.getResponseBodyAsString());

                        throw new EmbeddingGenerationException(
                                        "Failed to generate Gemini embedding.", ex);

                } catch (RestClientException ex) {

                        logger.error(
                                        "Gemini embedding API connection failed. Model: {}",
                                        model,
                                        ex);

                        throw new EmbeddingGenerationException(
                                        "Failed to connect to Gemini embedding API.", ex);
                }
        }

        private record EmbeddingApiResponse(
                        EmbeddingData embedding) {
        }

        private record EmbeddingData(
                        @JsonProperty("values") List<Double> values) {
        }
}
