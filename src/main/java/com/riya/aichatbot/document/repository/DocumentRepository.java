package com.riya.aichatbot.document.repository;

import com.riya.aichatbot.auth.entity.User;
import com.riya.aichatbot.document.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DocumentRepository extends JpaRepository<Document, Long> {

    List<Document> findByUserOrderByUploadedAtDesc(User user);

}