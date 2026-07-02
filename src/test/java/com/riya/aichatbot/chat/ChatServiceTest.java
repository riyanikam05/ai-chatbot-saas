package com.riya.aichatbot.chat;

import com.riya.aichatbot.ai.OllamaService;
import com.riya.aichatbot.chat.dto.ConversationResponse;
import com.riya.aichatbot.chat.dto.MessageResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatServiceTest {

    @Mock
    private ConversationRepository conversationRepository;

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private OllamaService ollamaService;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    @InjectMocks
    private ChatService chatService;

    @Test
    void testCreateConversation() {
        Long userId = 1L;
        Conversation conversation = Conversation.builder()
                .id(1L)
                .userId(userId)
                .title("New Chat")
                .build();

        when(conversationRepository.save(any(Conversation.class))).thenReturn(conversation);

        ConversationResponse response = chatService.createConversation(userId);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("New Chat", response.getTitle());
        verify(conversationRepository, times(1)).save(any(Conversation.class));
    }

    @Test
    void testSendMessage() {
        Long userId = 1L;
        Long conversationId = 1L;
        String userMessage = "Hello";

        Conversation conversation = Conversation.builder()
                .id(conversationId)
                .userId(userId)
                .title("New Chat")
                .build();

        Message userMsg = Message.builder()
                .id(1L)
                .conversationId(conversationId)
                .role("user")
                .content(userMessage)
                .build();

        Message assistantMsg = Message.builder()
                .id(2L)
                .conversationId(conversationId)
                .role("assistant")
                .content("Hi there!")
                .build();

        when(conversationRepository.findById(conversationId)).thenReturn(java.util.Optional.of(conversation));
        when(messageRepository.save(any(Message.class))).thenReturn(userMsg, assistantMsg);
        when(messageRepository.findByConversationIdOrderByCreatedAtAsc(conversationId))
                .thenReturn(List.of(userMsg));
        when(ollamaService.chat(anyList())).thenReturn("Hi there!");
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.increment(anyString())).thenReturn(1L);
        when(conversationRepository.save(any(Conversation.class))).thenReturn(conversation);

        MessageResponse response = chatService.sendMessage(userId, conversationId, userMessage);

        assertNotNull(response);
        assertEquals("assistant", response.getRole());
        assertEquals("Hi there!", response.getContent());
        verify(messageRepository, times(2)).save(any(Message.class));
    }

    @Test
    void testSendMessageAccessDenied() {
        Long userId = 1L;
        Long conversationId = 1L;
        String userMessage = "Hello";

        Conversation conversation = Conversation.builder()
                .id(conversationId)
                .userId(2L) // Different user
                .title("New Chat")
                .build();

        when(conversationRepository.findById(conversationId)).thenReturn(java.util.Optional.of(conversation));

        assertThrows(RuntimeException.class, () -> {
            chatService.sendMessage(userId, conversationId, userMessage);
        });
    }
}