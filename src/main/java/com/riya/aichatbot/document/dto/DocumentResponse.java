package com.riya.aichatbot.document.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class DocumentResponse {

    private Long id;
    private String originalFilename;
    private LocalDateTime uploadedAt;

}