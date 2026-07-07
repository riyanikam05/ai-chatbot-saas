package com.riya.aichatbot.chat;

import com.riya.aichatbot.ai.OllamaService;
import com.riya.aichatbot.chat.dto.ConversationResponse;
import com.riya.aichatbot.chat.dto.MessageResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ChatService {

    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final OllamaService ollamaService;

    public ChatService(
            ConversationRepository conversationRepository,
            MessageRepository messageRepository,
            OllamaService ollamaService
    ) {
        this.conversationRepository = conversationRepository;
        this.messageRepository = messageRepository;
        this.ollamaService = ollamaService;
    }

    @Transactional
    public MessageResponse sendMessage(Long userId, Long conversationId, String userMessage) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new RuntimeException("Conversation not found"));

        if (!conversation.getUserId().equals(userId)) {
            throw new RuntimeException("Access denied: conversation does not belong to user");
        }

        Message userMsg = Message.builder()
                .conversationId(conversationId)
                .role("user")
                .content(userMessage)
                .build();

        messageRepository.save(userMsg);

        List<Message> allHistory =
                messageRepository.findByConversationIdOrderByCreatedAtAsc(conversationId);

        List<Message> recentHistory = allHistory.size() > 20
                ? allHistory.subList(allHistory.size() - 20, allHistory.size())
                : allHistory;

        List<Map<String, String>> messageHistory = recentHistory.stream()
                .map(message -> Map.of(
                        "role", message.getRole(),
                        "content", message.getContent()
                ))
                .collect(Collectors.toList());

        String aiResponse = ollamaService.chat(messageHistory);

        Message assistantMsg = Message.builder()
                .conversationId(conversationId)
                .role("assistant")
                .content(aiResponse)
                .build();

        messageRepository.save(assistantMsg);

        conversation.setUpdatedAt(LocalDateTime.now());

        if (allHistory.size() == 1) {
            String title = userMessage.length() > 50
                    ? userMessage.substring(0, 50) + "..."
                    : userMessage;

            conversation.setTitle(title);
        }

        conversationRepository.save(conversation);

        return MessageResponse.builder()
                .id(assistantMsg.getId())
                .role(assistantMsg.getRole())
                .content(assistantMsg.getContent())
                .createdAt(assistantMsg.getCreatedAt())
                .build();
    }

    @Transactional
    public ConversationResponse createConversation(Long userId) {
        Conversation conversation = Conversation.builder()
                .userId(userId)
                .title("New Chat")
                .build();

        conversation = conversationRepository.save(conversation);

        return ConversationResponse.builder()
                .id(conversation.getId())
                .title(conversation.getTitle())
                .createdAt(conversation.getCreatedAt())
                .updatedAt(conversation.getUpdatedAt())
                .build();
    }

    public List<ConversationResponse> getConversations(Long userId) {
        return conversationRepository.findByUserIdOrderByUpdatedAtDesc(userId).stream()
                .map(conversation -> ConversationResponse.builder()
                        .id(conversation.getId())
                        .title(conversation.getTitle())
                        .createdAt(conversation.getCreatedAt())
                        .updatedAt(conversation.getUpdatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    public List<MessageResponse> getMessages(Long userId, Long conversationId) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new RuntimeException("Conversation not found"));

        if (!conversation.getUserId().equals(userId)) {
            throw new RuntimeException("Access denied: conversation does not belong to user");
        }

        return messageRepository.findByConversationIdOrderByCreatedAtAsc(conversationId).stream()
                .map(message -> MessageResponse.builder()
                        .id(message.getId())
                        .role(message.getRole())
                        .content(message.getContent())
                        .createdAt(message.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteConversation(Long userId, Long conversationId) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new RuntimeException("Conversation not found"));

        if (!conversation.getUserId().equals(userId)) {
            throw new RuntimeException("Access denied: conversation does not belong to user");
        }

        conversationRepository.delete(conversation);
    }
}