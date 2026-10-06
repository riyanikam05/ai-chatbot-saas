package com.riya.aichatbot.ai.dto;

import java.util.List;

public record EmbeddingResponse(

                String model,

                List<List<Double>> embeddings

) {
}