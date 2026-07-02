package com.riya.aichatbot.ai;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class OllamaService {

    @Value("${ollama.base-url}")
    private String baseUrl;

    @Value("${ollama.model}")
    private String model;

    private final RestTemplate restTemplate;

    public OllamaService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @PostConstruct
    public void printConfig() {
        System.out.println("====================================");
        System.out.println("Ollama Base URL = " + baseUrl);
        System.out.println("Ollama Model = " + model);
        System.out.println("====================================");
    }

    @SuppressWarnings("unchecked")
    public String chat(List<Map<String, String>> messageHistory) {

    String url = baseUrl + "/api/chat";

    Map<String, Object> request = Map.of(
            "model", model,
            "messages", messageHistory,
            "stream", false
    );

    System.out.println("================================");
    System.out.println("POSTING TO : " + url);
    System.out.println("REQUEST : " + request);
    System.out.println("================================");

    try {

        String response = restTemplate.postForObject(
                url,
                request,
                String.class
        );

        System.out.println("RAW RESPONSE:");
        System.out.println(response);

        return response;

    } catch (Exception e) {

        e.printStackTrace();

        throw new RuntimeException("Failed to get response from Ollama", e);
    }
}
}