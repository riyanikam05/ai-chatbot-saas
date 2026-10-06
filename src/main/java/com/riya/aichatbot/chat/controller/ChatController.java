package com.riya.aichatbot.chat.controller;

import com.riya.aichatbot.chat.dto.ChatRequest;
import com.riya.aichatbot.chat.dto.ChatResponse;
import com.riya.aichatbot.chat.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
@Tag(name = "Chat", description = "RAG Question Answering APIs")
@SecurityRequirement(name = "bearerAuth")
public class ChatController {

        private final ChatService chatService;

        public ChatController(ChatService chatService) {
                this.chatService = chatService;
        }

        @Operation(summary = "Ask a question", description = """
                        Searches the authenticated user's uploaded documents using
                        semantic vector search (ChromaDB) and generates an answer
                        using the Groq LLM.

                        Pass conversationId to continue an existing conversation,
                        or omit it to start a new one. The question and answer are
                        both saved as Messages in the resulting conversation.
                        """)
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Answer generated successfully", content = @Content(schema = @Schema(implementation = ChatResponse.class))),
                        @ApiResponse(responseCode = "400", description = "Invalid request"),
                        @ApiResponse(responseCode = "401", description = "Unauthorized"),
                        @ApiResponse(responseCode = "500", description = "Internal server error")
        })
        @PostMapping("/ask")
        public ResponseEntity<ChatResponse> askQuestion(
                        @Valid @RequestBody ChatRequest request,
                        Authentication authentication) {

                ChatResponse response = chatService.askQuestion(
                                request.getQuestion(),
                                request.getConversationId(),
                                authentication);

                return ResponseEntity.ok(response);
        }

}