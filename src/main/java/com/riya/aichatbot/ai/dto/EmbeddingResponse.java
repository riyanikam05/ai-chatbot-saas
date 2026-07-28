package com.riya.aichatbot.ai.dto;

import java.util.List;

public record EmbeddingResponse(
        List<Double> embedding
) {
}