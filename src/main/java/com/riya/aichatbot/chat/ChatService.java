package com.riya.aichatbot.chat;

import com.riya.aichatbot.ai.OllamaService;
import com.riya.aichatbot.chat.dto.MessageResponse;
import com.riya.aichatbot.chat.dto.ConversationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class ChatService {

    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final OllamaService ollamaService;
    private final RedisTemplate<String, Object> redisTemplate;

    public ChatService(ConversationRepository conversationRepository, MessageRepository messageRepository,
                       OllamaService ollamaService, @Autowired(required = false) RedisTemplate<String, Object> redisTemplate) {
        this.conversationRepository = conversationRepository;
        this.messageRepository = messageRepository;
        this.ollamaService = ollamaService;
        this.redisTemplate = redisTemplate;
    }

    @Transactional
    public MessageResponse sendMessage(Long userId, Long conversationId, String userMessage) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new RuntimeException("Conversation not found"));

        if (!conversation.getUserId().equals(userId)) {
            throw new RuntimeException("Access denied: conversation does not belong to user");
        }

        checkRateLimit(userId);

        Message userMsg = Message.builder()
                .conversationId(conversationId)
                .role("user")
                .content(userMessage)
                .build();
        messageRepository.save(userMsg);

        List<Message> allHistory = messageRepository.findByConversationIdOrderByCreatedAtAsc(conversationId);
        List<Message> history = allHistory.size() > 20 
                ? allHistory.subList(allHistory.size() - 20, allHistory.size())
                : allHistory;
        List<Map<String, String>> messageHistory = history.stream()
                .map(msg -> Map.of("role", msg.getRole(), "content", msg.getContent()))
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

    private void checkRateLimit(Long userId) {
        if (redisTemplate == null) {
            return; // Skip rate limiting if Redis is not available
        }
        
        String key = "ratelimit:" + userId;
        Long count = redisTemplate.opsForValue().increment(key);

        if (count != null && count == 1) {
            redisTemplate.expire(key, 1, TimeUnit.MINUTES);
        }

        if (count != null && count > 20) {
            throw new RuntimeException("Rate limit exceeded. You can send 20 messages per minute. Please wait a moment.");
        }
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
                .map(conv -> ConversationResponse.builder()
                        .id(conv.getId())
                        .title(conv.getTitle())
                        .createdAt(conv.getCreatedAt())
                        .updatedAt(conv.getUpdatedAt())
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
                .map(msg -> MessageResponse.builder()
                        .id(msg.getId())
                        .role(msg.getRole())
                        .content(msg.getContent())
                        .createdAt(msg.getCreatedAt())
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