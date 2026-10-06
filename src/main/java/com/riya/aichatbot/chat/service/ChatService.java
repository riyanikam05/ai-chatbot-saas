package com.riya.aichatbot.chat.service;

import com.riya.aichatbot.ai.service.ChromaService;
import com.riya.aichatbot.ai.service.GroqService;
import com.riya.aichatbot.ai.service.OllamaService;
import com.riya.aichatbot.auth.entity.User;
import com.riya.aichatbot.auth.repository.UserRepository;
import com.riya.aichatbot.chat.dto.ChatResponse;
import com.riya.aichatbot.chat.entity.Conversation;
import com.riya.aichatbot.chat.entity.Message;
import com.riya.aichatbot.chat.repository.MessageRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class ChatService {

        private final OllamaService ollamaService;
        private final ChromaService chromaService;
        private final GroqService groqService;
        private final UserRepository userRepository;
        private final ConversationService conversationService;
        private final MessageRepository messageRepository;

        public ChatService(
                        OllamaService ollamaService,
                        ChromaService chromaService,
                        GroqService groqService,
                        UserRepository userRepository,
                        ConversationService conversationService,
                        MessageRepository messageRepository) {

                this.ollamaService = ollamaService;
                this.chromaService = chromaService;
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

                // Resolve the conversation: reuse an existing one (verifying
                // ownership) or create a new one for this question.
                Conversation conversation = (conversationId != null)
                                ? conversationService.getConversation(conversationId, user)
                                : conversationService.createConversation(user, question);

                // Save the user's question as a Message right away, so it's
                // recorded even if generation fails below.
                messageRepository.save(Message.builder()
                                .conversationId(conversation.getId())
                                .role("user")
                                .content(question)
                                .build());

                // Generate embedding for the user's question
                List<Double> embedding = ollamaService.generateEmbedding(question);

                // Get Chroma collection
                String collectionId = chromaService.getCollectionId();

                // Search only this user's document chunks
                List<String> chunks = chromaService.searchRelevantChunks(
                                collectionId,
                                user.getId(),
                                embedding,
                                3);

                // No relevant context found
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

                // Build context
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

                String answer = groqService.chat(
                                List.of(
                                                Map.of(
                                                                "role", "user",
                                                                "content", prompt)));

                // Save the assistant's answer as a Message
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