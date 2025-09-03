package com.kycapp.kycbackend.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.kycapp.kycbackend.model.UserKyc;

public interface UserKycRepository extends MongoRepository<UserKyc, String> {
}
