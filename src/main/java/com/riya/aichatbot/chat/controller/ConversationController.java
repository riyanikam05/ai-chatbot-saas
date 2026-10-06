package com.riya.aichatbot.chat.controller;

import com.riya.aichatbot.auth.entity.User;
import com.riya.aichatbot.chat.dto.ChatRequest;
import com.riya.aichatbot.chat.dto.ConversationResponse;
import com.riya.aichatbot.chat.dto.MessageResponse;
import com.riya.aichatbot.chat.entity.Conversation;
import com.riya.aichatbot.chat.repository.MessageRepository;
import com.riya.aichatbot.chat.service.ConversationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/conversations")
@Tag(name = "Conversations", description = "Manage chat conversations")
@SecurityRequirement(name = "bearerAuth")
public class ConversationController {

        private final ConversationService conversationService;
        private final MessageRepository messageRepository;

        public ConversationController(
                        ConversationService conversationService,
                        MessageRepository messageRepository) {

                this.conversationService = conversationService;
                this.messageRepository = messageRepository;
        }

        @Operation(summary = "Create a new conversation")
        @PostMapping
        public ResponseEntity<ConversationResponse> createConversation(
                        @Valid @RequestBody ChatRequest request,
                        Authentication authentication) {

                User user = (User) authentication.getPrincipal();

                Conversation conversation = conversationService.createConversation(
                                user, request.getQuestion());

                return ResponseEntity.ok(toResponse(conversation));
        }

        @Operation(summary = "List the authenticated user's conversations")
        @GetMapping
        public ResponseEntity<List<ConversationResponse>> getUserConversations(
                        Authentication authentication) {

                User user = (User) authentication.getPrincipal();

                List<ConversationResponse> responses = conversationService
                                .getUserConversations(user)
                                .stream()
                                .map(this::toResponse)
                                .toList();

                return ResponseEntity.ok(responses);
        }

        @Operation(summary = "Get a single conversation by id")
        @GetMapping("/{id}")
        public ResponseEntity<ConversationResponse> getConversation(
                        @PathVariable Long id,
                        Authentication authentication) {

                User user = (User) authentication.getPrincipal();
                Conversation conversation = conversationService.getConversation(id, user);

                return ResponseEntity.ok(toResponse(conversation));
        }

        @Operation(summary = "Get all messages in a conversation, oldest first")
        @GetMapping("/{id}/messages")
        public ResponseEntity<List<MessageResponse>> getConversationMessages(
                        @PathVariable Long id,
                        Authentication authentication) {

                User user = (User) authentication.getPrincipal();

                // Ownership check: throws if the conversation doesn't exist or
                // doesn't belong to this user, before any messages are returned.
                conversationService.getConversation(id, user);

                List<MessageResponse> messages = messageRepository
                                .findByConversationIdOrderByCreatedAtAsc(id)
                                .stream()
                                .map(this::toResponse)
                                .toList();

                return ResponseEntity.ok(messages);
        }

        @Operation(summary = "Delete a conversation")
        @DeleteMapping("/{id}")
        public ResponseEntity<Void> deleteConversation(
                        @PathVariable Long id,
                        Authentication authentication) {

                User user = (User) authentication.getPrincipal();
                conversationService.deleteConversation(id, user);
                return ResponseEntity.noContent().build();
        }

        private ConversationResponse toResponse(Conversation conversation) {
                return ConversationResponse.builder()
                                .id(conversation.getId())
                                .title(conversation.getTitle())
                                .createdAt(conversation.getCreatedAt())
                                .updatedAt(conversation.getUpdatedAt())
                                .build();
        }

        private MessageResponse toResponse(com.riya.aichatbot.chat.entity.Message message) {
                return MessageResponse.builder()
                                .id(message.getId())
                                .role(message.getRole())
                                .content(message.getContent())
                                .createdAt(message.getCreatedAt())
                                .build();
        }
}