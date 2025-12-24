package com.ragdemo.service;

import org.springframework.stereotype.Service;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

@Service
public class EmbeddingService {
    
    // Simulated embedding - in real system, this would use BERT/SentenceTransformers
    public String generateEmbeddingHash(String text) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(text.getBytes());
            return bytesToHex(hash).substring(0, 32); // Truncate for simplicity
        } catch (NoSuchAlgorithmException e) {
            return Integer.toHexString(text.hashCode());
        }
    }
    
    // Simulate semantic similarity search
    public List<String> findSimilarChunks(String query, List<String> allChunks) {
        List<ScoredChunk> scored = new ArrayList<>();
        String queryHash = generateEmbeddingHash(query);
        
        for (String chunk : allChunks) {
            double score = calculateSimilarity(queryHash, generateEmbeddingHash(chunk));
            scored.add(new ScoredChunk(chunk, score));
        }
        
        scored.sort((a, b) -> Double.compare(b.score, a.score));
        
        List<String> result = new ArrayList<>();
        for (int i = 0; i < Math.min(3, scored.size()); i++) {
            result.add(scored.get(i).chunk);
        }
        
        return result;
    }
    
    private double calculateSimilarity(String hash1, String hash2) {
        // Simple similarity based on common characters
        int matches = 0;
        for (int i = 0; i < Math.min(hash1.length(), hash2.length()); i++) {
            if (hash1.charAt(i) == hash2.charAt(i)) {
                matches++;
            }
        }
        return (double) matches / Math.max(hash1.length(), hash2.length());
    }
    
    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
    
    private static class ScoredChunk {
        String chunk;
        double score;
        
        ScoredChunk(String chunk, double score) {
            this.chunk = chunk;
            this.score = score;
        }
    }
}