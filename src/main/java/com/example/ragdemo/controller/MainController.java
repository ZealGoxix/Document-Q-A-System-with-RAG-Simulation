package com.ragdemo.controller;

import com.ragdemo.model.Document;
import com.ragdemo.model.QAResponse;
import com.ragdemo.service.DocumentService;
import com.ragdemo.service.QAService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;

@Controller
public class MainController {
    
    @Autowired
    private DocumentService documentService;
    
    @Autowired
    private QAService qaService;
    
    @GetMapping("/")
    public String home(Model model) {
        List<Document> documents = documentService.getAllDocuments();
        model.addAttribute("documents", documents);
        return "index";
    }
    
    @PostMapping("/upload")
    public String handleUpload(@RequestParam("file") MultipartFile file, Model model) {
        try {
            if (file.isEmpty()) {
                model.addAttribute("error", "Please select a file to upload");
                return home(model);
            }
            
            Document document = documentService.processUpload(file);
            model.addAttribute("message", "File uploaded successfully: " + document.getFilename());
            
        } catch (IOException e) {
            model.addAttribute("error", "Error processing file: " + e.getMessage());
        }
        
        return home(model);
    }
    
    @PostMapping("/ask")
    @ResponseBody
    public QAResponse askQuestion(@RequestParam("question") String question) {
        return qaService.answerQuestion(question);
    }
}