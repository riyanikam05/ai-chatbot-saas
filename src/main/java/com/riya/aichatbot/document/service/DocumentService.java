package com.riya.aichatbot.document.service;

import com.riya.aichatbot.ai.service.ChromaService;
import com.riya.aichatbot.ai.service.OllamaService;
import com.riya.aichatbot.auth.entity.User;
import com.riya.aichatbot.document.entity.Document;
import com.riya.aichatbot.document.repository.DocumentRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.UUID;

@Service
public class DocumentService {

    private static final Logger logger = LoggerFactory.getLogger(DocumentService.class);

    private final DocumentRepository documentRepository;
    private final PdfExtractorService pdfExtractorService;
    private final TextChunkingService textChunkingService;
    private final OllamaService ollamaService;
    private final ChromaService chromaService;

    @Value("${file.upload-dir}")
    private String uploadDir;

    public DocumentService(
            DocumentRepository documentRepository,
            PdfExtractorService pdfExtractorService,
            TextChunkingService textChunkingService,
            OllamaService ollamaService,
            ChromaService chromaService) {

        this.documentRepository = documentRepository;
        this.pdfExtractorService = pdfExtractorService;
        this.textChunkingService = textChunkingService;
        this.ollamaService = ollamaService;
        this.chromaService = chromaService;
    }

    @Transactional
    public Document uploadDocument(MultipartFile file, User user) throws IOException {

        Path uploadPath = Paths.get(uploadDir);
        Files.createDirectories(uploadPath);

        String storedFilename = UUID.randomUUID() + "_" + file.getOriginalFilename();

        Path destination = uploadPath.resolve(storedFilename);

        Files.copy(
                file.getInputStream(),
                destination,
                StandardCopyOption.REPLACE_EXISTING);

        Document document = new Document();
        document.setOriginalFilename(file.getOriginalFilename());
        document.setStoredFilename(storedFilename);
        document.setFilePath(destination.toString());
        document.setFileSize(file.getSize());
        document.setUser(user);

        document = documentRepository.save(document);

        String text = pdfExtractorService.extractText(destination.toFile());

        List<String> chunks = textChunkingService.chunkText(text);

        String collectionId = chromaService.getCollectionId();

        logger.info("Total chunks = {}", chunks.size());

        for (int i = 0; i < chunks.size(); i++) {
            logger.info("------------- Chunk {} -------------", i);
            logger.info(chunks.get(i));
        }
        for (int i = 0; i < chunks.size(); i++) {

            String chunk = chunks.get(i);

            List<Double> embedding = ollamaService.generateEmbedding(chunk);

            chromaService.storeEmbedding(
                    collectionId,
                    document.getId(),
                    user.getId(),
                    i,
                    chunk,
                    embedding);
        }

        return document;
    }

    public List<Document> getUserDocuments(User user) {
        return documentRepository.findByUserOrderByUploadedAtDesc(user);
    }
}