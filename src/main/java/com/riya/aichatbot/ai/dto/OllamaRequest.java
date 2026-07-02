package com.riya.aichatbot.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OllamaRequest {
    private String model;
    private List<Map<String, String>> messages;
    private boolean stream;
}