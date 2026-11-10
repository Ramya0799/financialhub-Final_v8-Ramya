package com.maheshpai.financialhub.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@RestControllerAdvice
public class UploadExceptionHandler {
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<String> uploadTooLarge(MaxUploadSizeExceededException ex) {
        return ResponseEntity.status(413).body("Photo is too large. Please select a photo smaller than 50 MB.");
    }
}
