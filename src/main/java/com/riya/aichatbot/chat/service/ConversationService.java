package com.riya.aichatbot.chat.service;

import com.riya.aichatbot.auth.entity.User;
import com.riya.aichatbot.chat.entity.Conversation;
import com.riya.aichatbot.chat.repository.ConversationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConversationService {

    private final ConversationRepository conversationRepository;

    public ConversationService(
            ConversationRepository conversationRepository) {

        this.conversationRepository = conversationRepository;
    }

    public Conversation createConversation(
            User user,
            String firstQuestion) {

        Conversation conversation = new Conversation();

        conversation.setUser(user);

        String title = firstQuestion.length() > 50
                ? firstQuestion.substring(0, 50)
                : firstQuestion;

        conversation.setTitle(title);

        return conversationRepository.save(conversation);
    }

    public List<Conversation> getUserConversations(User user) {
        return conversationRepository.findByUserOrderByUpdatedAtDesc(user);
    }

    public Conversation getConversation(
            Long id,
            User user) {

        return conversationRepository
                .findByIdAndUser(id, user)
                .orElseThrow(() -> new RuntimeException("Conversation not found"));
    }

    public void deleteConversation(
            Long id,
            User user) {

        Conversation conversation = getConversation(id, user);

        conversationRepository.delete(conversation);
    }

}