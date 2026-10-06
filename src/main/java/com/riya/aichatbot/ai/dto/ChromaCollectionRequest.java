package com.riya.aichatbot.ai.dto;

public class ChromaCollectionRequest {

    private String name;

    public ChromaCollectionRequest() {
    }

    public ChromaCollectionRequest(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}