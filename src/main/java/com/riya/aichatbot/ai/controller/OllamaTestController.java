package com.riya.aichatbot.ai.controller;

import com.riya.aichatbot.ai.service.OllamaService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class OllamaTestController {

    private final OllamaService ollamaService;

    public OllamaTestController(OllamaService ollamaService) {
        this.ollamaService = ollamaService;
    }

    @GetMapping("/api/test/embedding")
    public List<Double> testEmbedding() {
        return ollamaService.generateEmbedding("What is Git?");
    }

}