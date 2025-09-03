package com.kycapp.kycbackend.controller;

import com.kycapp.kycbackend.model.UserKyc;
import com.kycapp.kycbackend.repository.UserKycRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/kyc")
public class UserKycController {

    @Autowired
    private UserKycRepository userKycRepository;

    // Create new KYC record
    @PostMapping
    public UserKyc createKyc(@RequestBody UserKyc userKyc) {
    System.out.println("Received KYC: " + userKyc);
    return userKycRepository.save(userKyc);
}

    // Get all KYC records
    @GetMapping
    public List<UserKyc> getAllKycs() {
        return userKycRepository.findAll();
    }

    // Get single KYC by ID
    @GetMapping("/{id}")
    public UserKyc getKycById(@PathVariable String id) {
        return userKycRepository.findById(id).orElse(null);
    }

    // Delete KYC by ID
    @DeleteMapping("/{id}")
    public String deleteKyc(@PathVariable String id) {
        userKycRepository.deleteById(id);
        return "KYC record deleted with id: " + id;
    }
}
