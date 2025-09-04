package com.kycapp.kycbackend.controller;

import com.kycapp.kycbackend.model.Upload;
import com.kycapp.kycbackend.service.UploadService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/api/uploads")
public class UploadController {

    private final UploadService uploadService;

    public UploadController(UploadService uploadService) {
        this.uploadService = uploadService;
    }

    // 1. Initiate Upload
    @PostMapping("/initiate")
    public ResponseEntity<Map<String, Object>> initiateUpload(@RequestBody Map<String, Object> request) {
        String filename = (String) request.get("filename");
        String contentType = (String) request.get("contentType");
        int totalChunks = (int) request.get("totalChunks");

        Upload upload = uploadService.initiateUpload(filename, contentType, totalChunks);

        Map<String, Object> response = new HashMap<>();
        response.put("uploadId", upload.getId());
        response.put("status", upload.getStatus());

        return ResponseEntity.ok(response);
    }

    // 2. Upload a Chunk
    @PostMapping("/{uploadId}/chunk")
    public ResponseEntity<Map<String, Object>> uploadChunk(
            @PathVariable String uploadId,
            @RequestParam("chunkIndex") int chunkIndex,
            @RequestParam("file") MultipartFile file) throws IOException {

        uploadService.storeChunk(uploadId, chunkIndex, file);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Chunk uploaded successfully");
        response.put("chunkIndex", chunkIndex);

        return ResponseEntity.ok(response);
    }

    // 3. Get Upload Status
    @GetMapping("/{uploadId}/status")
    public ResponseEntity<Map<String, Object>> getUploadStatus(@PathVariable String uploadId) {
        Optional<Upload> uploadOpt = uploadService.getUploadStatus(uploadId);

        if (uploadOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Upload upload = uploadOpt.get();

        Map<String, Object> response = new HashMap<>();
        response.put("uploadId", upload.getId());
        response.put("uploadedChunks", upload.getUploadedChunks());
        response.put("remainingChunks", getRemainingChunks(upload));
        response.put("status", upload.getStatus());

        return ResponseEntity.ok(response);
    }

    private List<Integer> getRemainingChunks(Upload upload) {
        List<Integer> remaining = new ArrayList<>();
        for (int i = 1; i <= upload.getTotalChunks(); i++) {
            if (!upload.getUploadedChunks().contains(i)) {
                remaining.add(i);
            }
        }
        return remaining;
    }

    // 4. Complete Upload
    @PostMapping("/{uploadId}/complete")
    public ResponseEntity<Map<String, Object>> completeUpload(@PathVariable String uploadId) {
        uploadService.markUploadCompleted(uploadId);

        Map<String, Object> response = new HashMap<>();
        response.put("uploadId", uploadId);
        response.put("status", "COMPLETED");

        return ResponseEntity.ok(response);
    }
}
