
package com.riya.aichatbot.document.service;

import com.riya.aichatbot.ai.service.GeminiEmbeddingService;
import com.riya.aichatbot.auth.entity.User;
import com.riya.aichatbot.document.entity.Document;
import com.riya.aichatbot.document.repository.DocumentChunkRepository;
import com.riya.aichatbot.document.repository.DocumentRepository;
import com.riya.aichatbot.exception.DocumentNotFoundException;

import jakarta.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.support.TransactionSynchronization;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
public class DocumentService {

    private static final Logger logger = LoggerFactory.getLogger(DocumentService.class);

    private final DocumentRepository documentRepository;
    private final PdfExtractorService pdfExtractorService;
    private final TextChunkingService textChunkingService;
    private final GeminiEmbeddingService geminiEmbeddingService;
    private final DocumentChunkRepository documentChunkRepository;
    private final Path uploadPath;

    public DocumentService(
            DocumentRepository documentRepository,
            PdfExtractorService pdfExtractorService,
            TextChunkingService textChunkingService,
            GeminiEmbeddingService geminiEmbeddingService,
            DocumentChunkRepository documentChunkRepository,
            @Value("${file.upload-dir}") String uploadDir) {

        this.documentRepository = documentRepository;
        this.pdfExtractorService = pdfExtractorService;
        this.textChunkingService = textChunkingService;
        this.geminiEmbeddingService = geminiEmbeddingService;
        this.documentChunkRepository = documentChunkRepository;

        this.uploadPath = Paths.get(uploadDir)
                .toAbsolutePath()
                .normalize();
    }

    @Transactional
    public Document uploadDocument(MultipartFile file, User user) {

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException(
                    "Uploaded file is empty.");
        }

        Path destination = null;
        boolean uploadSuccessful = false;

        try {
            Files.createDirectories(uploadPath);

            String originalFilename = file.getOriginalFilename();
            String storedFilename = UUID.randomUUID() + ".pdf";

            destination = uploadPath.resolve(storedFilename).normalize();

            if (!destination.startsWith(uploadPath)) {
                throw new IllegalArgumentException(
                        "Invalid file path.");
            }

            try (var inputStream = file.getInputStream()) {
                Files.copy(inputStream, destination);
            }

            Document document = new Document();
            document.setOriginalFilename(originalFilename);
            document.setStoredFilename(storedFilename);
            document.setFilePath(destination.toString());
            document.setFileSize(file.getSize());
            document.setUser(user);

            document = documentRepository.save(document);

            String extractedText = pdfExtractorService.extractText(destination.toFile());

            List<String> chunks = textChunkingService.chunkText(extractedText);

            if (chunks.isEmpty()) {
                throw new IllegalArgumentException(
                        "The uploaded PDF contains no extractable text.");
            }

            for (int i = 0; i < chunks.size(); i++) {

                String chunk = chunks.get(i);

                var embedding = geminiEmbeddingService.generateDocumentEmbedding(chunk);

                documentChunkRepository.save(
                        document.getId(),
                        user.getId(),
                        i,
                        chunk,
                        embedding);
            }

            logger.info(
                    "Document {} processed successfully with {} chunks.",
                    document.getId(),
                    chunks.size());

            uploadSuccessful = true;

            return document;

        } catch (IOException ex) {

            logger.error(
                    "Failed to process uploaded document.",
                    ex);

            throw new RuntimeException(
                    "Failed to upload document.", ex);

        } finally {

            if (!uploadSuccessful && destination != null) {
                try {
                    Files.deleteIfExists(destination);

                    logger.info(
                            "Cleaned up failed document upload: {}",
                            destination);

                } catch (IOException cleanupException) {

                    logger.error(
                            "Failed to clean up uploaded file: {}",
                            destination,
                            cleanupException);
                }
            }
        }
    }

    public List<Document> getUserDocuments(User user) {

        return documentRepository
                .findByUserOrderByUploadedAtDesc(user);
    }

    @Transactional
    public void deleteDocument(Long id, User user) {

        Document document = documentRepository
                .findByIdAndUser(id, user)
                .orElseThrow(() -> new DocumentNotFoundException(
                        "Document not found"));

        Path filePath = Paths.get(document.getFilePath())
                .toAbsolutePath()
                .normalize();

        if (!filePath.startsWith(uploadPath)) {
            throw new IllegalStateException(
                    "Invalid document file path.");
        }

        documentRepository.delete(document);

        // Delete the physical file only after the database transaction commits.
        if (TransactionSynchronizationManager.isSynchronizationActive()) {

            TransactionSynchronizationManager.registerSynchronization(
                    new TransactionSynchronization() {

                        @Override
                        public void afterCommit() {
                            deletePhysicalFile(filePath);
                        }
                    });

        } else {
            throw new IllegalStateException(
                    "Document deletion requires an active transaction.");
        }
    }

    private void deletePhysicalFile(Path filePath) {

        try {
            Files.deleteIfExists(filePath);

            logger.info(
                    "Deleted document file: {}",
                    filePath);

        } catch (IOException ex) {

            logger.error(
                    "Database record deleted, but physical file cleanup failed: {}",
                    filePath,
                    ex);
        }
    }
}
