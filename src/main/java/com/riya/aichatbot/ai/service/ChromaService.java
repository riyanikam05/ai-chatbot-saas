package com.riya.aichatbot.ai.service;

import com.riya.aichatbot.ai.dto.ChromaAddRequest;
import com.riya.aichatbot.ai.dto.ChromaCollectionRequest;
import com.riya.aichatbot.ai.dto.ChromaCollectionResponse;
import com.riya.aichatbot.ai.dto.ChromaQueryRequest;
import com.riya.aichatbot.ai.dto.ChromaQueryResponse;
import com.riya.aichatbot.exception.ChromaException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.List;
import java.util.Map;

@Service
public class ChromaService {

        private static final Logger logger = LoggerFactory.getLogger(ChromaService.class);

        private final RestClient restClient;

        @Value("${chroma.base-url}")
        private String baseUrl;

        @Value("${chroma.tenant}")
        private String tenant;

        @Value("${chroma.database}")
        private String database;

        @Value("${chroma.collection}")
        private String collectionName;

        public ChromaService(RestClient.Builder builder) {
                this.restClient = builder.build();
        }

        public String getCollectionId() {

                try {

                        ChromaCollectionResponse response = restClient.get()
                                        .uri(baseUrl
                                                        + "/api/v2/tenants/" + tenant
                                                        + "/databases/" + database
                                                        + "/collections/" + collectionName)
                                        .retrieve()
                                        .body(ChromaCollectionResponse.class);

                        if (response == null || response.getId() == null) {
                                throw new ChromaException("Failed to retrieve Chroma collection.");
                        }

                        return response.getId();

                } catch (RestClientException ex) {

                        logger.error("Failed to connect to ChromaDB.", ex);

                        throw new ChromaException(
                                        "Failed to connect to ChromaDB.",
                                        ex);

                } catch (Exception ex) {

                        logger.error("Failed to retrieve Chroma collection.", ex);

                        throw new ChromaException(
                                        "Failed to retrieve Chroma collection.",
                                        ex);
                }
        }

        public void createCollectionIfNotExists() {

                try {

                        restClient.post()
                                        .uri(baseUrl
                                                        + "/api/v2/tenants/" + tenant
                                                        + "/databases/" + database
                                                        + "/collections")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .body(new ChromaCollectionRequest(collectionName))
                                        .retrieve()
                                        .toBodilessEntity();

                        logger.info("Created Chroma collection: {}", collectionName);

                } catch (RestClientException ex) {

                        logger.info("Chroma collection already exists.");
                }
        }

        public void storeEmbedding(
                        String collectionId,
                        Long documentId,
                        Long userId,
                        int chunkIndex,
                        String chunk,
                        List<Double> embedding) {

                try {

                        Map<String, Object> metadata = Map.of(
                                        "documentId", documentId,
                                        "userId", userId,
                                        "chunkIndex", chunkIndex);

                        ChromaAddRequest request = new ChromaAddRequest(
                                        List.of(documentId + "-" + chunkIndex),
                                        List.of(embedding),
                                        List.of(chunk),
                                        List.of(metadata));

                        restClient.post()
                                        .uri(baseUrl
                                                        + "/api/v2/tenants/" + tenant
                                                        + "/databases/" + database
                                                        + "/collections/" + collectionId
                                                        + "/add")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .body(request)
                                        .retrieve()
                                        .toBodilessEntity();

                } catch (RestClientException ex) {

                        logger.error("Failed to connect to ChromaDB.", ex);

                        throw new ChromaException(
                                        "Failed to store embedding in ChromaDB.",
                                        ex);

                } catch (Exception ex) {

                        logger.error("Failed to store embedding.", ex);

                        throw new ChromaException(
                                        "Failed to store embedding in ChromaDB.",
                                        ex);
                }
        }

        public List<String> searchRelevantChunks(
                        String collectionId,
                        Long userId,
                        List<Double> embedding,
                        int topK) {

                try {

                        ChromaQueryRequest request = new ChromaQueryRequest(
                                        List.of(embedding),
                                        topK,
                                        List.of("documents"),
                                        Map.of("userId", userId));

                        ChromaQueryResponse response = restClient.post()
                                        .uri(baseUrl
                                                        + "/api/v2/tenants/" + tenant
                                                        + "/databases/" + database
                                                        + "/collections/" + collectionId
                                                        + "/query")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .body(request)
                                        .retrieve()
                                        .body(ChromaQueryResponse.class);

                        if (response == null
                                        || response.getDocuments() == null
                                        || response.getDocuments().isEmpty()) {

                                return List.of();
                        }

                        return response.getDocuments()
                                        .get(0)
                                        .stream()
                                        .distinct()
                                        .limit(topK)
                                        .toList();

                } catch (RestClientException ex) {

                        logger.error("Failed to query ChromaDB.", ex);

                        throw new ChromaException(
                                        "Failed to query ChromaDB.",
                                        ex);

                } catch (Exception ex) {

                        logger.error("Semantic search failed.", ex);

                        throw new ChromaException(
                                        "Semantic search failed.",
                                        ex);
                }
        }
}