package com.kycapp.kycbackend.repository;

import com.kycapp.kycbackend.model.Upload;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UploadRepository extends MongoRepository<Upload, String> {
}
