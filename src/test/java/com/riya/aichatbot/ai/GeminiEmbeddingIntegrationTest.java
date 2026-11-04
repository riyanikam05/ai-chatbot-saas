
package com.riya.aichatbot.ai;

import com.riya.aichatbot.ai.service.GeminiEmbeddingService;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GeminiEmbeddingIntegrationTest {

    private static GeminiEmbeddingService embeddingService;

    @BeforeAll
    static void setUp() {

        String apiKey = System.getenv("GEMINI_API_KEY");

        assertNotNull(apiKey, "GEMINI_API_KEY environment variable is missing.");
        assertFalse(apiKey.isBlank(), "GEMINI_API_KEY is empty.");

        embeddingService = new GeminiEmbeddingService(
                RestClient.builder(),
                apiKey,
                "gemini-embedding-001",
                768);
    }

    @Test
    void testGeminiDocumentEmbedding() {

        String sampleText = "Artificial intelligence helps computers understand human language.";

        List<Double> embedding = embeddingService.generateDocumentEmbedding(sampleText);

        assertNotNull(embedding);
        assertEquals(768, embedding.size());

        assertTrue(
                embedding.stream().allMatch(
                        value -> value != null && Double.isFinite(value)),
                "Embedding contains invalid values.");

        double norm = Math.sqrt(
                embedding.stream()
                        .mapToDouble(value -> value * value)
                        .sum());

        assertEquals(1.0, norm, 0.000001,
                "Embedding should be normalized.");

        System.out.println("Gemini document embedding test passed.");
        System.out.println("Dimensions: " + embedding.size());
        System.out.println("Normalized vector length: " + norm);
    }

    @Test
    void testGeminiQueryEmbedding() {

        String sampleText = "How does artificial intelligence work?";

        List<Double> embedding = embeddingService.generateQueryEmbedding(sampleText);

        assertNotNull(embedding);
        assertEquals(768, embedding.size());

        assertTrue(
                embedding.stream().allMatch(
                        value -> value != null && Double.isFinite(value)),
                "Embedding contains invalid values.");

        System.out.println("Gemini query embedding test passed.");
    }
}
