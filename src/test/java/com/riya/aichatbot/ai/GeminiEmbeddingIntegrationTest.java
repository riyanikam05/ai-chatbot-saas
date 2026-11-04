
package com.riya.aichatbot.ai;

import com.riya.aichatbot.ai.service.GeminiEmbeddingService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.web.client.RestClient;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@EnabledIfSystemProperty(named = "gemini.integration", matches = "true")
class GeminiEmbeddingIntegrationTest {

        private static GeminiEmbeddingService embeddingService;

        @BeforeAll
        static void setUp() {
                String apiKey = System.getenv("GEMINI_API_KEY");

                assertNotNull(
                                apiKey,
                                "GEMINI_API_KEY environment variable must be set to run Gemini integration tests");

                assertFalse(
                                apiKey.isBlank(),
                                "GEMINI_API_KEY must not be blank");

                embeddingService = new GeminiEmbeddingService(
                                RestClient.builder(),
                                apiKey,
                                "gemini-embedding-001",
                                768);
        }

        @Test
        void testGeminiDocumentEmbedding() {
                String text = "This is a test document for Gemini embeddings.";

                List<Double> embedding = embeddingService.generateDocumentEmbedding(text);

                assertNotNull(embedding, "Document embedding should not be null");
                assertEquals(768, embedding.size(), "Embedding should contain 768 dimensions");

                assertTrue(
                                embedding.stream().allMatch(Double::isFinite),
                                "All embedding values should be finite");

                double norm = Math.sqrt(
                                embedding.stream()
                                                .mapToDouble(value -> value * value)
                                                .sum());

                assertEquals(
                                1.0,
                                norm,
                                0.000001,
                                "Document embedding should be normalized");

                System.out.println("Gemini document embedding test passed.");
                System.out.println("Embedding dimensions: " + embedding.size());
                System.out.println("Embedding norm: " + norm);
        }

        @Test
        void testGeminiQueryEmbedding() {
                String query = "How does a retrieval augmented generation system work?";

                List<Double> embedding = embeddingService.generateQueryEmbedding(query);

                assertNotNull(embedding, "Query embedding should not be null");
                assertEquals(768, embedding.size(), "Embedding should contain 768 dimensions");

                assertTrue(
                                embedding.stream().allMatch(Double::isFinite),
                                "All embedding values should be finite");

                System.out.println("Gemini query embedding test passed.");
                System.out.println("Embedding dimensions: " + embedding.size());
        }
}
