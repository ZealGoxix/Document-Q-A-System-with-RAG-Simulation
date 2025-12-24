package com.example.ragdemo.service;

import com.example.ragdemo.model.QAResponse;
import com.example.ragdemo.model.TextChunk;
import com.example.ragdemo.repository.ChunkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class QAService {
    
    @Autowired
    private ChunkRepository chunkRepository;
    
    @Autowired
    private EmbeddingService embeddingService;
    
    public QAResponse answerQuestion(String question) {
        long startTime = System.currentTimeMillis();
        
        // Get all chunks
        List<TextChunk> allChunks = chunkRepository.findAll();
        List<String> chunkTexts = allChunks.stream()
            .map(TextChunk::getText)
            .collect(Collectors.toList());
        
        // Find relevant chunks
        List<String> relevantChunks = embeddingService.findSimilarChunks(question, chunkTexts);
        
        // Generate answer (simulated)
        String answer = generateAnswer(question, relevantChunks);
        
        // Calculate confidence
        double confidence = calculateConfidence(question, answer);
        
        QAResponse response = new QAResponse();
        response.setQuestion(question);
        response.setAnswer(answer);
        response.setSourceChunks(relevantChunks);
        response.setConfidence(confidence);
        response.setProcessingTimeMs(System.currentTimeMillis() - startTime);
        
        return response;
    }
    
    private String generateAnswer(String question, List<String> relevantChunks) {
        if (relevantChunks.isEmpty()) {
            return "I don't have enough information to answer that question. Please upload relevant documents first.";
        }
        
        // Simple rule-based answer generation
        StringBuilder answer = new StringBuilder();
        answer.append("Based on the documents, ");
        
        // Check for common question types
        String lowerQuestion = question.toLowerCase();
        
        if (lowerQuestion.contains("what") && lowerQuestion.contains("mean")) {
            answer.append("this typically means: ");
            answer.append(relevantChunks.get(0));
        } else if (lowerQuestion.contains("how") || lowerQuestion.contains("process")) {
            answer.append("the process involves: ");
            for (int i = 0; i < Math.min(2, relevantChunks.size()); i++) {
                answer.append("\n").append(i + 1).append(". ").append(relevantChunks.get(i));
            }
        } else if (lowerQuestion.contains("why")) {
            answer.append("the reason is: ");
            answer.append(relevantChunks.get(0));
        } else {
            answer.append("here's what I found: ");
            for (int i = 0; i < Math.min(2, relevantChunks.size()); i++) {
                answer.append("\n").append(relevantChunks.get(i));
            }
        }
        
        return answer.toString();
    }
    
    private double calculateConfidence(String question, String answer) {
        // Simulated confidence score
        Random rand = new Random(question.hashCode());
        return 0.7 + rand.nextDouble() * 0.3; // Between 0.7 and 1.0
    }
}