package com.riya.aichatbot.ai.dto;

import java.util.List;

public class ChromaQueryResponse {

    private List<List<String>> ids;
    private List<List<String>> documents;

    public ChromaQueryResponse() {
    }

    public List<List<String>> getIds() {
        return ids;
    }

    public void setIds(List<List<String>> ids) {
        this.ids = ids;
    }

    public List<List<String>> getDocuments() {
        return documents;
    }

    public void setDocuments(List<List<String>> documents) {
        this.documents = documents;
    }
}