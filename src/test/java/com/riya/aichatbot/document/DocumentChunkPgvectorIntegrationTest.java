
package com.riya.aichatbot.document;

import com.riya.aichatbot.ai.service.GeminiEmbeddingService;
import com.riya.aichatbot.document.repository.DocumentChunkRepository;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class DocumentChunkPgvectorIntegrationTest {

    private static JdbcTemplate jdbcTemplate;
    private static DocumentChunkRepository chunkRepository;
    private static GeminiEmbeddingService embeddingService;

    private Long userId;
    private Long documentId;

    @BeforeAll
    static void setUp() {

        String databaseUrl = System.getenv().getOrDefault(
                "SPRING_DATASOURCE_URL",
                "jdbc:postgresql://localhost:5434/mydatabase");

        String username = System.getenv().getOrDefault(
                "SPRING_DATASOURCE_USERNAME", "postgres");

        String password = System.getenv().getOrDefault(
                "SPRING_DATASOURCE_PASSWORD", "postgres");

        String geminiApiKey = System.getenv("GEMINI_API_KEY");

        assertNotNull(geminiApiKey,
                "GEMINI_API_KEY environment variable is missing.");

        assertFalse(geminiApiKey.isBlank(),
                "GEMINI_API_KEY is empty.");

        DriverManagerDataSource dataSource = new DriverManagerDataSource(
                databaseUrl,
                username,
                password);

        jdbcTemplate = new JdbcTemplate(dataSource);

        chunkRepository = new DocumentChunkRepository(jdbcTemplate);

        embeddingService = new GeminiEmbeddingService(
                RestClient.builder(),
                geminiApiKey,
                "gemini-embedding-001",
                768);
    }

    @BeforeEach
    void createTestRecords() {

        String uniqueEmail = "pgvector-test-" + UUID.randomUUID() + "@example.com";

        userId = jdbcTemplate.queryForObject(
                """
                        INSERT INTO users (name, email, password)
                        VALUES (?, ?, ?)
                        RETURNING id
                        """,
                Long.class,
                "PGVector Integration Test",
                uniqueEmail,
                "test-password");

        documentId = jdbcTemplate.queryForObject(
                """
                        INSERT INTO documents
                            (user_id, file_path, file_size, content_type)
                        VALUES (?, ?, ?, ?)
                        RETURNING id
                        """,
                Long.class,
                userId,
                "pgvector-integration-test/" + UUID.randomUUID() + ".pdf",
                1L,
                "application/pdf");
    }

    @AfterEach
    void cleanUpTestRecords() {

        if (documentId != null) {
            jdbcTemplate.update(
                    "DELETE FROM document_chunks WHERE document_id = ?",
                    documentId);

            jdbcTemplate.update(
                    "DELETE FROM documents WHERE id = ?",
                    documentId);
        }

        if (userId != null) {
            jdbcTemplate.update(
                    "DELETE FROM users WHERE id = ?",
                    userId);
        }

        documentId = null;
        userId = null;
    }

    @Test
    void testGeminiEmbeddingStorageAndVectorSearch() {

        String documentText = "PostgreSQL supports vector similarity search using pgvector. "
                + "It can store embeddings and retrieve semantically similar content.";

        // Generate a real document embedding using Gemini.
        List<Double> documentEmbedding = embeddingService.generateDocumentEmbedding(documentText);

        assertEquals(768, documentEmbedding.size());

        // Store the embedding in the actual PostgreSQL database.
        chunkRepository.save(
                documentId,
                userId,
                0,
                documentText,
                documentEmbedding);

        // Generate a real query embedding using Gemini.
        List<Double> queryEmbedding = embeddingService.generateQueryEmbedding(
                "How does PostgreSQL perform vector similarity search?");

        assertEquals(768, queryEmbedding.size());

        // Retrieve the most relevant chunk using pgvector.
        List<String> results = chunkRepository.searchRelevantChunks(
                userId,
                queryEmbedding,
                3);

        assertFalse(results.isEmpty(),
                "pgvector should retrieve the stored document chunk.");

        assertTrue(results.contains(documentText),
                "Retrieved content should match the stored chunk.");

        // Verify that another user cannot retrieve this chunk.
        List<String> unauthorizedResults = chunkRepository.searchRelevantChunks(
                userId + 999999L,
                queryEmbedding,
                3);

        assertTrue(unauthorizedResults.isEmpty(),
                "Chunks belonging to another user must not be returned.");

        System.out.println("Gemini + PostgreSQL pgvector integration passed.");
        System.out.println("Embedding dimensions: " + documentEmbedding.size());
        System.out.println("Retrieved chunks: " + results.size());
        System.out.println("User isolation: verified.");
    }
}
