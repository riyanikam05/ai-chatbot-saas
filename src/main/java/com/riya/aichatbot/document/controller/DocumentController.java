package com.riya.aichatbot.document.controller;

import com.riya.aichatbot.auth.entity.User;
import com.riya.aichatbot.auth.repository.UserRepository;
import com.riya.aichatbot.document.dto.DocumentResponse;
import com.riya.aichatbot.document.dto.UploadResponse;
import com.riya.aichatbot.document.entity.Document;
import com.riya.aichatbot.document.service.DocumentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/documents")
@Tag(name = "Documents", description = "Upload and manage source documents for RAG")
@SecurityRequirement(name = "bearerAuth")
public class DocumentController {

        private final DocumentService documentService;
        private final UserRepository userRepository;

        public DocumentController(
                        DocumentService documentService,
                        UserRepository userRepository) {

                this.documentService = documentService;
                this.userRepository = userRepository;
        }

        private User getCurrentUser(Authentication authentication) {

                return userRepository.findByEmail(authentication.getName())
                                .orElseThrow(() -> new RuntimeException("User not found."));
        }

        @Operation(summary = "Upload a PDF document for RAG ingestion")
        // Declaring consumes = MULTIPART_FORM_DATA_VALUE here is what tells
        // springdoc to render this as a file-picker widget in Swagger UI
        // instead of a raw JSON body box.
        @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
        public ResponseEntity<UploadResponse> uploadDocument(
                        @RequestParam("file") MultipartFile file,
                        Authentication authentication) throws IOException {

                User user = getCurrentUser(authentication);

                Document document = documentService.uploadDocument(file, user);

                UploadResponse response = new UploadResponse(
                                document.getId(),
                                document.getOriginalFilename(),
                                "Document uploaded successfully.");

                return ResponseEntity.ok(response);
        }

        @Operation(summary = "List the authenticated user's uploaded documents")
        @GetMapping
        public ResponseEntity<List<DocumentResponse>> getDocuments(
                        Authentication authentication) {

                User user = getCurrentUser(authentication);

                List<DocumentResponse> responses = documentService
                                .getUserDocuments(user)
                                .stream()
                                .map(this::toResponse)
                                .toList();

                return ResponseEntity.ok(responses);
        }

        private DocumentResponse toResponse(Document document) {
                return DocumentResponse.builder()
                                .id(document.getId())
                                .originalFilename(document.getOriginalFilename())
                                .fileSize(document.getFileSize())
                                .uploadedAt(document.getUploadedAt())
                                .build();
        }
}