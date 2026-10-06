package com.riya.aichatbot.ai.dto;

public class OllamaEmbeddingRequest {

    private String model;
    private String input;

    public OllamaEmbeddingRequest() {
    }

    public OllamaEmbeddingRequest(String model, String input) {
        this.model = model;
        this.input = input;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }
}