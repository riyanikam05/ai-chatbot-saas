
package com.riya.aichatbot.document.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class DocumentChunkRepository {

    private final JdbcTemplate jdbcTemplate;

    public DocumentChunkRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void save(
            Long documentId,
            Long userId,
            int chunkIndex,
            String content,
            List<Double> embedding) {

        validateEmbedding(embedding);

        String vector = toVectorLiteral(embedding);

        jdbcTemplate.update("""
                INSERT INTO document_chunks
                    (document_id, user_id, chunk_index, content, embedding)
                VALUES (?, ?, ?, ?, CAST(? AS vector))
                ON CONFLICT (document_id, chunk_index)
                DO UPDATE SET
                    content = EXCLUDED.content,
                    embedding = EXCLUDED.embedding
                """,
                documentId,
                userId,
                chunkIndex,
                content,
                vector);
    }

    public List<String> searchRelevantChunks(
            Long userId,
            List<Double> embedding,
            int topK) {

        validateEmbedding(embedding);

        if (topK < 1 || topK > 20) {
            throw new IllegalArgumentException(
                    "topK must be between 1 and 20.");
        }

        String vector = toVectorLiteral(embedding);

        return jdbcTemplate.queryForList("""
                SELECT content
                FROM document_chunks
                WHERE user_id = ?
                ORDER BY embedding <=> CAST(? AS vector)
                LIMIT ?
                """,
                String.class,
                userId,
                vector,
                topK);
    }

    private void validateEmbedding(List<Double> embedding) {
        if (embedding == null || embedding.size() != 768
                || embedding.stream().anyMatch(
                        value -> value == null || !Double.isFinite(value))) {
            throw new IllegalArgumentException(
                    "Embedding must contain exactly 768 finite values.");
        }
    }

    private String toVectorLiteral(List<Double> embedding) {
        return embedding.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(",", "[", "]"));
    }
}
