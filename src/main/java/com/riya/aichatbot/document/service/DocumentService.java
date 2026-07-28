package com.riya.aichatbot.document.service;

import com.riya.aichatbot.document.dto.DocumentResponse;
import com.riya.aichatbot.document.entity.Document;
import com.riya.aichatbot.document.repository.DocumentRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class DocumentService {

    private static final Logger log = LoggerFactory.getLogger(DocumentService.class);

    private final DocumentRepository documentRepository;
    private final PdfExtractionService pdfService;
    private final TextChunkingService textChunkingService;

    @Value("${app.upload-dir}")
    private String uploadDir;

    public DocumentService(DocumentRepository documentRepository,
                           PdfExtractionService pdfService,
                           TextChunkingService textChunkingService) {
        this.documentRepository = documentRepository;
        this.pdfService = pdfService;
        this.textChunkingService = textChunkingService;
    }

    @Transactional
    public DocumentResponse uploadDocument(Long userId, MultipartFile file) throws IOException {

        // Validate file
        if (file.isEmpty()) {
            throw new RuntimeException("Please select a PDF file.");
        }

        if (!"application/pdf".equals(file.getContentType())) {
            throw new RuntimeException("Only PDF files are allowed.");
        }

        // Create uploads directory if it doesn't exist
        Path uploadPath = Paths.get(uploadDir);
        Files.createDirectories(uploadPath);

        // Generate unique filename
        String storedFilename = UUID.randomUUID() + ".pdf";

        Path destination = uploadPath.resolve(storedFilename);

        // Save PDF locally
        Files.copy(
                file.getInputStream(),
                destination,
                StandardCopyOption.REPLACE_EXISTING
        );

        // Extract text from PDF
        String extractedText = pdfService.extractText(destination.toString());

        log.info("Successfully extracted {} characters from '{}'",
                extractedText.length(),
                file.getOriginalFilename());

        // Split text into chunks
        List<String> chunks = textChunkingService.chunkText(extractedText);

        log.info("Created {} chunks from '{}'",
                chunks.size(),
                file.getOriginalFilename());

        /*
         * NEXT STEP:
         *
         * for (String chunk : chunks) {
         *      embeddingService.generateEmbedding(chunk);
         * }
         *
         * Then store the embeddings in ChromaDB.
         */

        // Save document metadata
        Document document = Document.builder()
                .userId(userId)
                .originalFilename(file.getOriginalFilename())
                .storedFilename(storedFilename)
                .filePath(destination.toString())
                .build();

        document = documentRepository.save(document);

        return DocumentResponse.builder()
                .id(document.getId())
                .originalFilename(document.getOriginalFilename())
                .uploadedAt(document.getUploadedAt())
                .build();
    }

    public List<DocumentResponse> getDocuments(Long userId) {

        return documentRepository.findByUserIdOrderByUploadedAtDesc(userId)
                .stream()
                .map(document -> DocumentResponse.builder()
                        .id(document.getId())
                        .originalFilename(document.getOriginalFilename())
                        .uploadedAt(document.getUploadedAt())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteDocument(Long userId, Long documentId) {

        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document not found."));

        if (!document.getUserId().equals(userId)) {
            throw new RuntimeException("Access denied.");
        }

        try {
            Files.deleteIfExists(Paths.get(document.getFilePath()));
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete PDF file.");
        }

        documentRepository.delete(document);
    }
}