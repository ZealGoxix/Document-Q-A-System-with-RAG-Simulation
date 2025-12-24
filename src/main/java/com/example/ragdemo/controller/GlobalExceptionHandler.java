package com.example.ragdemo.exception;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@ControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String handleMaxSizeException(MaxUploadSizeExceededException exc, Model model) {
        model.addAttribute("error", "File too large! Maximum size is 10MB");
        return "index";
    }
    
    @ExceptionHandler(Exception.class)
    public String handleGenericException(Exception exc, Model model) {
        System.err.println("Error occurred: " + exc.getMessage());
        exc.printStackTrace();
        model.addAttribute("error", "An error occurred: " + exc.getMessage());
        return "index";
    }
}