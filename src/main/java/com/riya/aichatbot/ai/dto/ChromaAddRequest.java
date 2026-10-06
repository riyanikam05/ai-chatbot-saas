package com.riya.aichatbot.ai.dto;

import java.util.List;
import java.util.Map;

public class ChromaAddRequest {

    private List<String> ids;

    private List<List<Double>> embeddings;

    private List<String> documents;

    private List<Map<String, Object>> metadatas;

    public ChromaAddRequest() {
    }

    public ChromaAddRequest(
            List<String> ids,
            List<List<Double>> embeddings,
            List<String> documents,
            List<Map<String, Object>> metadatas
    ) {
        this.ids = ids;
        this.embeddings = embeddings;
        this.documents = documents;
        this.metadatas = metadatas;
    }

    public List<String> getIds() {
        return ids;
    }

    public void setIds(List<String> ids) {
        this.ids = ids;
    }

    public List<List<Double>> getEmbeddings() {
        return embeddings;
    }

    public void setEmbeddings(List<List<Double>> embeddings) {
        this.embeddings = embeddings;
    }

    public List<String> getDocuments() {
        return documents;
    }

    public void setDocuments(List<String> documents) {
        this.documents = documents;
    }

    public List<Map<String, Object>> getMetadatas() {
        return metadatas;
    }

    public void setMetadatas(List<Map<String, Object>> metadatas) {
        this.metadatas = metadatas;
    }

}