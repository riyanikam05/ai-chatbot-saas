package com.riya.aichatbot.ai.dto;

public record EmbeddingRequest(
        String model,
        String prompt
) {
}