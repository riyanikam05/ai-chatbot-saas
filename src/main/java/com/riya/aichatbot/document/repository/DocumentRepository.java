package com.riya.aichatbot.document.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.riya.aichatbot.document.entity.Document;

import java.util.List;

public interface DocumentRepository extends JpaRepository<Document, Long> {

    List<Document> findByUserIdOrderByUploadedAtDesc(Long userId);

}