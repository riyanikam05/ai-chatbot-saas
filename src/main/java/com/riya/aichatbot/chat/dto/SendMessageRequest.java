package com.riya.aichatbot.chat.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SendMessageRequest {
    @NotBlank(message = "Message cannot be empty")
    private String message;
}