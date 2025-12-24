package com.example.ragdemo.repository;

import com.example.ragdemo.model.TextChunk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ChunkRepository extends JpaRepository<TextChunk, Long> {
    
    @Query("SELECT c FROM TextChunk c WHERE c.document.id = :documentId ORDER BY c.chunkIndex")
    List<TextChunk> findByDocumentId(@Param("documentId") Long documentId);
    
    @Query("SELECT c FROM TextChunk c WHERE c.embeddingHash = :hash")
    List<TextChunk> findByEmbeddingHash(@Param("hash") String hash);
}