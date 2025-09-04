package com.kycapp.kycbackend.controller;

import com.kycapp.kycbackend.model.UserKyc;
import com.kycapp.kycbackend.repository.UserKycRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/kyc")
public class UserKycController {

    @Autowired
    private UserKycRepository userKycRepository;

    // ✅ Create new KYC record with validation
    @PostMapping
    public ResponseEntity<?> createKyc(@Valid @RequestBody UserKyc userKyc, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(result.getAllErrors());
        }
        return ResponseEntity.ok(userKycRepository.save(userKyc));
    }

    // ✅ Update KYC by ID
    @PutMapping("/{id}")
    public ResponseEntity<?> updateKyc(@PathVariable String id, @Valid @RequestBody UserKyc updatedKyc, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(result.getAllErrors());
        }

        return userKycRepository.findById(id).map(existingKyc -> {
            existingKyc.setName(updatedKyc.getName());
            existingKyc.setAadhaarNumber(updatedKyc.getAadhaarNumber());
            existingKyc.setPanNumber(updatedKyc.getPanNumber());
            existingKyc.setPhone(updatedKyc.getPhone());
            existingKyc.setEmail(updatedKyc.getEmail());
            return ResponseEntity.ok(userKycRepository.save(existingKyc));
        }).orElse(ResponseEntity.notFound().build());
    }

    // ✅ Get all KYC records
    @GetMapping
    public List<UserKyc> getAllKycs() {
        return userKycRepository.findAll();
    }

    // ✅ Get single KYC by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getKycById(@PathVariable String id) {
        return userKycRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ✅ Delete KYC by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteKyc(@PathVariable String id) {
        if (userKycRepository.existsById(id)) {
            userKycRepository.deleteById(id);
            return ResponseEntity.ok("KYC record deleted with id: " + id);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
