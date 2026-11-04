
package com.riya.aichatbot.chat.service;

import com.riya.aichatbot.ai.service.GeminiEmbeddingService;
import com.riya.aichatbot.ai.service.GroqService;
import com.riya.aichatbot.auth.entity.User;
import com.riya.aichatbot.auth.repository.UserRepository;
import com.riya.aichatbot.chat.dto.ChatResponse;
import com.riya.aichatbot.chat.entity.Conversation;
import com.riya.aichatbot.chat.entity.Message;
import com.riya.aichatbot.chat.repository.MessageRepository;
import com.riya.aichatbot.document.repository.DocumentChunkRepository;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class ChatService {

        private final GeminiEmbeddingService geminiEmbeddingService;
        private final DocumentChunkRepository documentChunkRepository;
        private final GroqService groqService;
        private final UserRepository userRepository;
        private final ConversationService conversationService;
        private final MessageRepository messageRepository;

        public ChatService(
                        GeminiEmbeddingService geminiEmbeddingService,
                        DocumentChunkRepository documentChunkRepository,
                        GroqService groqService,
                        UserRepository userRepository,
                        ConversationService conversationService,
                        MessageRepository messageRepository) {

                this.geminiEmbeddingService = geminiEmbeddingService;
                this.documentChunkRepository = documentChunkRepository;
                this.groqService = groqService;
                this.userRepository = userRepository;
                this.conversationService = conversationService;
                this.messageRepository = messageRepository;
        }

        @Transactional
        public ChatResponse askQuestion(
                        String question,
                        Long conversationId,
                        Authentication authentication) {

                // Get logged-in user
                User user = userRepository.findByEmail(authentication.getName())
                                .orElseThrow(() -> new RuntimeException("User not found"));

                // Resolve conversation and verify ownership
                Conversation conversation = (conversationId != null)
                                ? conversationService.getConversation(conversationId, user)
                                : conversationService.createConversation(user, question);

                // Save user's question
                messageRepository.save(Message.builder()
                                .conversationId(conversation.getId())
                                .role("user")
                                .content(question)
                                .build());

                // Generate Gemini embedding for the question
                List<Double> embedding = geminiEmbeddingService.generateQueryEmbedding(question);

                // Search only the logged-in user's document chunks
                List<String> chunks = documentChunkRepository.searchRelevantChunks(
                                user.getId(),
                                embedding,
                                3);

                // Handle missing context
                if (chunks.isEmpty()) {

                        String noContextAnswer = "I couldn't find any relevant information in your uploaded documents.";

                        messageRepository.save(Message.builder()
                                        .conversationId(conversation.getId())
                                        .role("assistant")
                                        .content(noContextAnswer)
                                        .build());

                        return new ChatResponse(
                                        conversation.getId(),
                                        question,
                                        noContextAnswer,
                                        List.of());
                }

                // Build context from retrieved chunks
                String context = String.join("\n\n", chunks);

                String prompt = """
                                You are a helpful AI assistant.

                                Answer ONLY from the provided context.

                                If the answer is not contained in the context,
                                reply exactly:

                                I couldn't find that information in the uploaded documents.

                                -------------------------
                                Context:
                                %s
                                -------------------------

                                Question:
                                %s
                                """.formatted(context, question);

                // Generate answer using Groq
                String answer = groqService.chat(
                                List.of(
                                                Map.of(
                                                                "role", "user",
                                                                "content", prompt)));

                // Save assistant's answer
                messageRepository.save(Message.builder()
                                .conversationId(conversation.getId())
                                .role("assistant")
                                .content(answer)
                                .build());

                return new ChatResponse(
                                conversation.getId(),
                                question,
                                answer,
                                chunks);
        }
}
