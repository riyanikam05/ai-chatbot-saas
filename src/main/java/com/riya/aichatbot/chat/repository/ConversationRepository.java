package com.riya.aichatbot.chat.repository;

import com.riya.aichatbot.auth.entity.User;
import com.riya.aichatbot.chat.entity.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {

    List<Conversation> findByUserOrderByUpdatedAtDesc(User user);

    Optional<Conversation> findByIdAndUser(Long id, User user);

}