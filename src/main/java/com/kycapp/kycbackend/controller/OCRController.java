package com.kycapp.kycbackend.controller;

import com.kycapp.kycbackend.service.OCRService;
import net.sourceforge.tess4j.TesseractException;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/ocr")
public class OCRController {

    @Autowired
    private OCRService ocrService;

    // Upload a file and extract text
    @PostMapping("/extract")
    public ResponseEntity<?> extractText(@RequestParam("file") MultipartFile file) {
        try {
            ObjectId[] outId = new ObjectId[1];
            String extractedText = ocrService.extractText(file.getInputStream(), file.getOriginalFilename(), outId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("filename", file.getOriginalFilename());
            response.put("documentId", outId[0].toString());
            response.put("extractedText", extractedText);

            return ResponseEntity.ok(response);
        } catch (IOException | TesseractException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }

    // Extract text from existing GridFS document
    @GetMapping("/extract/{documentId}")
    public ResponseEntity<?> extractTextFromGridFS(@PathVariable String documentId) {
        try {
            String extractedText = ocrService.extractTextFromGridFS(documentId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("documentId", documentId);
            response.put("extractedText", extractedText);

            return ResponseEntity.ok(response);
        } catch (IOException | TesseractException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }
}
