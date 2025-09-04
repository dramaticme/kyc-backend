package com.kycapp.kycbackend.service;

import com.kycapp.kycbackend.model.Upload;
import com.kycapp.kycbackend.repository.UploadRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Optional;

@Service
public class UploadService {

    private final UploadRepository uploadRepository;

    public UploadService(UploadRepository uploadRepository) {
        this.uploadRepository = uploadRepository;
    }

    // Start a new upload
    public Upload initiateUpload(String filename, String contentType, int totalChunks) {
        Upload upload = new Upload();
        upload.setFilename(filename);
        upload.setContentType(contentType);
        upload.setTotalChunks(totalChunks);
        upload.setStatus("IN_PROGRESS");
        return uploadRepository.save(upload);
    }

    // Store an uploaded chunk
    public void storeChunk(String uploadId, int chunkIndex, MultipartFile file) throws IOException {
        Optional<Upload> optUpload = uploadRepository.findById(uploadId);
        if (optUpload.isPresent()) {
            Upload upload = optUpload.get();

            // Save chunk to temp folder
            File dir = new File("uploads/" + uploadId);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            File chunkFile = new File(dir, "chunk_" + chunkIndex);
            try (FileOutputStream fos = new FileOutputStream(chunkFile)) {
                fos.write(file.getBytes());
            }

            // Update DB record
            upload.getUploadedChunks().add(chunkIndex);
            uploadRepository.save(upload);
        }
    }

    // Check upload status
    public Optional<Upload> getUploadStatus(String uploadId) {
        return uploadRepository.findById(uploadId);
    }

    // Mark entire upload as completed
    public void markUploadCompleted(String uploadId) {
        Optional<Upload> optUpload = uploadRepository.findById(uploadId);
        if (optUpload.isPresent()) {
            Upload upload = optUpload.get();
            upload.setStatus("COMPLETED");
            uploadRepository.save(upload);
        }
    }
}
