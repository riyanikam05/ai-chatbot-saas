package com.riya.aichatbot.document.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TextChunkingService {

    private static final int CHUNK_SIZE = 1000;

    public List<String> chunkText(String text) {

        List<String> chunks = new ArrayList<>();

        if (text == null || text.isBlank()) {
            return chunks;
        }

        text = text.replace("\r", " ")
                .replace("\n", " ")
                .replaceAll("\\s+", " ")
                .trim();

        int start = 0;

        while (start < text.length()) {

            int end = Math.min(start + CHUNK_SIZE, text.length());

            if (end < text.length()) {

                int lastSpace = text.lastIndexOf(" ", end);

                if (lastSpace > start) {
                    end = lastSpace;
                }
            }

            chunks.add(text.substring(start, end).trim());

            start = end;
        }

        return chunks;
    }
}