package com.riya.aichatbot.document.controller;

import com.riya.aichatbot.auth.entity.User;
import com.riya.aichatbot.auth.repository.UserRepository;
import com.riya.aichatbot.document.dto.DocumentResponse;
import com.riya.aichatbot.document.service.DocumentService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    private final DocumentService documentService;
    private final UserRepository userRepository;

    public DocumentController(DocumentService documentService,
                              UserRepository userRepository) {
        this.documentService = documentService;
        this.userRepository = userRepository;
    }

    private Long getCurrentUserId(Authentication authentication) {

        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        return user.getId();
    }

    @PostMapping("/upload")
    public ResponseEntity<DocumentResponse> uploadDocument(
            @RequestParam("file") MultipartFile file,
            Authentication authentication
    ) throws IOException {

        Long userId = getCurrentUserId(authentication);

        DocumentResponse response =
                documentService.uploadDocument(userId, file);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<DocumentResponse>> getDocuments(
            Authentication authentication
    ) {

        Long userId = getCurrentUserId(authentication);

        return ResponseEntity.ok(
                documentService.getDocuments(userId)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDocument(
            @PathVariable Long id,
            Authentication authentication
    ) {

        Long userId = getCurrentUserId(authentication);

        documentService.deleteDocument(userId, id);

        return ResponseEntity.noContent().build();
    }
}