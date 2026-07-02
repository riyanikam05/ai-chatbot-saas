package com.riya.aichatbot.chat;

import com.riya.aichatbot.auth.User;
import com.riya.aichatbot.auth.UserRepository;
import com.riya.aichatbot.chat.dto.ConversationResponse;
import com.riya.aichatbot.chat.dto.MessageResponse;
import com.riya.aichatbot.chat.dto.SendMessageRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/conversations")
public class ChatController {

    private final ChatService chatService;
    private final UserRepository userRepository;

    public ChatController(ChatService chatService, UserRepository userRepository) {
        this.chatService = chatService;
        this.userRepository = userRepository;
    }

    private Long getCurrentUserId(Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        return user.getId();
    }

    @PostMapping
    public ResponseEntity<ConversationResponse> createConversation(Authentication authentication) {
        Long userId = getCurrentUserId(authentication);
        ConversationResponse response = chatService.createConversation(userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<ConversationResponse>> getConversations(Authentication authentication) {
        Long userId = getCurrentUserId(authentication);
        List<ConversationResponse> conversations = chatService.getConversations(userId);
        return ResponseEntity.ok(conversations);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteConversation(@PathVariable Long id, Authentication authentication) {
        Long userId = getCurrentUserId(authentication);
        chatService.deleteConversation(userId, id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/messages")
    public ResponseEntity<List<MessageResponse>> getMessages(@PathVariable Long id, Authentication authentication) {
        Long userId = getCurrentUserId(authentication);
        List<MessageResponse> messages = chatService.getMessages(userId, id);
        return ResponseEntity.ok(messages);
    }

    @PostMapping("/{id}/messages")
    public ResponseEntity<MessageResponse> sendMessage(@PathVariable Long id, 
                                                        @Valid @RequestBody SendMessageRequest request,
                                                        Authentication authentication) {
        Long userId = getCurrentUserId(authentication);
        MessageResponse response = chatService.sendMessage(userId, id, request.getMessage());
        return ResponseEntity.ok(response);
    }
}