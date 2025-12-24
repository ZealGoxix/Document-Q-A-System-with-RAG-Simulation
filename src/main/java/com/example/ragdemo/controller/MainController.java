package com.example.ragdemo.controller;

import com.example.ragdemo.model.Document;
import com.example.ragdemo.model.QAResponse;
import com.example.ragdemo.service.DocumentService;
import com.example.ragdemo.service.QAService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Date;

@Controller
@RequestMapping("/")  // Explicit mapping for root
public class MainController {
    
    @Autowired
    private DocumentService documentService;
    
    @Autowired
    private QAService qaService;
    
    @GetMapping
    public String home(Model model) {
        List<Document> documents = documentService.getAllDocuments();
        model.addAttribute("documents", documents);
        return "index";
    }
    
    @PostMapping("/upload")
    public String handleUpload(@RequestParam("file") MultipartFile file, Model model) {
        try {
            System.out.println("Upload endpoint called. File: " + file.getOriginalFilename());
            
            if (file.isEmpty()) {
                model.addAttribute("error", "Please select a file to upload");
                return home(model);
            }
            
            if (file.getSize() > 10 * 1024 * 1024) { // 10MB limit
                model.addAttribute("error", "File size exceeds 10MB limit");
                return home(model);
            }
            
            // Validate file type
            String contentType = file.getContentType();
            String filename = file.getOriginalFilename();
            
            if (!isValidFileType(contentType, filename)) {
                model.addAttribute("error", "Only PDF and text files are allowed");
                return home(model);
            }
            
            // Process the file
            Document document = new Document();
            document.setFilename(filename);
            document.setContentType(contentType);
            document.setFileSize(file.getSize());
            
            // For text files, read content directly
            // For PDFs, we would need PDFBox - but for now, handle text only
            String content;
            if (filename.toLowerCase().endsWith(".txt")) {
                content = new String(file.getBytes(), StandardCharsets.UTF_8);
            } else {
                // For demo purposes, simulate PDF content
                content = "PDF content simulation for: " + filename + "\n\n" +
                         "This is a simulated document content. In a real application, " +
                         "you would use Apache PDFBox to extract text from PDF files. " +
                         "For now, this simulation allows you to test the RAG system.";
            }
            
            document.setContent(content);
            document.setUploadedAt(java.time.LocalDateTime.now());
            
            // Save document
            Document savedDoc = documentService.saveDocument(document);
            
            // Process chunks
            documentService.processDocumentChunks(savedDoc);
            
            model.addAttribute("message", "File uploaded successfully: " + savedDoc.getFilename());
            model.addAttribute("uploadedFile", savedDoc);
            
        } catch (Exception e) {
            System.err.println("Upload error: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "Error processing file: " + e.getMessage());
        }
        
        return home(model);
    }
    
    private boolean isValidFileType(String contentType, String filename) {
        if (filename == null) return false;
        
        String lowerFilename = filename.toLowerCase();
        return lowerFilename.endsWith(".txt") || 
               lowerFilename.endsWith(".pdf") ||
               lowerFilename.endsWith(".text") ||
               "text/plain".equals(contentType) ||
               "application/pdf".equals(contentType);
    }
    
    @PostMapping("/ask")
    @ResponseBody
    public QAResponse askQuestion(@RequestParam("question") String question) {
        System.out.println("Question received: " + question);
        return qaService.answerQuestion(question);
    }

    @GetMapping("/test")
    @ResponseBody
    public String test() {
        return "Server is running! " + new Date();
    }

    @GetMapping("/health")
    @ResponseBody
    public Map<String, String> health() {
        Map<String, String> status = new HashMap<>();
        status.put("status", "UP");
        status.put("timestamp", new Date().toString());
        status.put("service", "Document Q&A System");
        return status;
    }
}