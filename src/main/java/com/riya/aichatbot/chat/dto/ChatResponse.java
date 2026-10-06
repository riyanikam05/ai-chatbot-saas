package com.riya.aichatbot.chat.dto;

import java.util.List;

public record ChatResponse(

                Long conversationId,

                String question,

                String answer,

                List<String> retrievedChunks

) {
}