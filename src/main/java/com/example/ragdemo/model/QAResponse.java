package com.ragdemo.model;

import java.util.List;

public class QAResponse {
    private String question;
    private String answer;
    private List<String> sourceChunks;
    private double confidence;
    private long processingTimeMs;
    
    // Getters and setters
    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }
    
    public String getAnswer() { return answer; }
    public void setAnswer(String answer) { this.answer = answer; }
    
    public List<String> getSourceChunks() { return sourceChunks; }
    public void setSourceChunks(List<String> sourceChunks) { this.sourceChunks = sourceChunks; }
    
    public double getConfidence() { return confidence; }
    public void setConfidence(double confidence) { this.confidence = confidence; }
    
    public long getProcessingTimeMs() { return processingTimeMs; }
    public void setProcessingTimeMs(long processingTimeMs) { this.processingTimeMs = processingTimeMs; }
}