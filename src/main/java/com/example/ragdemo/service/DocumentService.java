package com.ragdemo.service;

import com.ragdemo.model.Document;
import com.ragdemo.model.TextChunk;
import com.ragdemo.repository.DocumentRepository;
import com.ragdemo.repository.ChunkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDateTime;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class DocumentService {
    
    @Autowired
    private DocumentRepository documentRepository;
    
    @Autowired
    private ChunkRepository chunkRepository;
    
    @Autowired
    private EmbeddingService embeddingService;
    
    public Document processUpload(MultipartFile file) throws IOException {
        Document document = new Document();
        document.setFilename(file.getOriginalFilename());
        document.setContentType(file.getContentType());
        document.setFileSize(file.getSize());
        
        String content = new String(file.getBytes(), StandardCharsets.UTF_8);
        document.setContent(content);
        document.setUploadedAt(LocalDateTime.now());
        
        Document savedDoc = documentRepository.save(document);
        
        // Chunk the document
        List<String> chunks = chunkText(content);
        saveChunks(savedDoc, chunks);
        
        return savedDoc;
    }
    
    private List<String> chunkText(String text) {
        List<String> chunks = new ArrayList<>();
        String[] sentences = text.split("[.!?]+\\s*");
        
        StringBuilder currentChunk = new StringBuilder();
        int sentenceCount = 0;
        
        for (String sentence : sentences) {
            if (sentence.trim().isEmpty()) continue;
            
            currentChunk.append(sentence).append(". ");
            sentenceCount++;
            
            if (sentenceCount >= 3 || currentChunk.length() > 500) {
                chunks.add(currentChunk.toString().trim());
                currentChunk = new StringBuilder();
                sentenceCount = 0;
            }
        }
        
        if (currentChunk.length() > 0) {
            chunks.add(currentChunk.toString().trim());
        }
        
        return chunks;
    }
    
    private void saveChunks(Document document, List<String> chunks) {
        for (int i = 0; i < chunks.size(); i++) {
            TextChunk chunk = new TextChunk();
            chunk.setDocument(document);
            chunk.setText(chunks.get(i));
            chunk.setChunkIndex(i);
            
            // Generate simulated embedding hash
            String hash = embeddingService.generateEmbeddingHash(chunks.get(i));
            chunk.setEmbeddingHash(hash);
            
            chunkRepository.save(chunk);
        }
    }
    
    public List<Document> getAllDocuments() {
        return documentRepository.findAll();
    }
}