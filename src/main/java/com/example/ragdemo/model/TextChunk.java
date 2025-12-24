package com.example.ragdemo.model;

import jakarta.persistence.*;

@Entity
@Table(name = "text_chunks")
public class TextChunk {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "document_id")
    private Document document;
    
    @Column(columnDefinition = "TEXT")
    private String text;
    
    private int chunkIndex;
    
    // Simulated vector embedding (in real system would be float array)
    private String embeddingHash;
    
    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Document getDocument() { return document; }
    public void setDocument(Document document) { this.document = document; }
    
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
    
    public int getChunkIndex() { return chunkIndex; }
    public void setChunkIndex(int chunkIndex) { this.chunkIndex = chunkIndex; }
    
    public String getEmbeddingHash() { return embeddingHash; }
    public void setEmbeddingHash(String embeddingHash) { this.embeddingHash = embeddingHash; }
}