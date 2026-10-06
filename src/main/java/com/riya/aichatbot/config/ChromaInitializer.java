package com.riya.aichatbot.config;

import com.riya.aichatbot.ai.service.ChromaService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class ChromaInitializer implements CommandLineRunner {

    private final ChromaService chromaService;

    public ChromaInitializer(ChromaService chromaService) {
        this.chromaService = chromaService;
    }

    @Override
    public void run(String... args) {
        chromaService.createCollectionIfNotExists();
    }
}