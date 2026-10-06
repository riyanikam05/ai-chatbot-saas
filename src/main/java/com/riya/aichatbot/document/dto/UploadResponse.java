package com.riya.aichatbot.document.dto;

public class UploadResponse {

    private Long id;
    private String fileName;
    private String message;

    public UploadResponse(Long id, String fileName, String message) {
        this.id = id;
        this.fileName = fileName;
        this.message = message;
    }

    public Long getId() {
        return id;
    }

    public String getFileName() {
        return fileName;
    }

    public String getMessage() {
        return message;
    }
}