package com.kycapp.kycbackend.controller;

import com.kycapp.kycbackend.model.User;
import com.kycapp.kycbackend.repository.UserRepository;
import com.kycapp.kycbackend.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private BCryptPasswordEncoder encoder;

    // Signup: create new user with hashed password
    @PostMapping("/signup")
    public Map<String, String> signup(@RequestBody User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }

        user.setPassword(encoder.encode(user.getPassword()));
        userRepository.save(user);

        return Map.of("message", "User registered successfully");
    }

    // Login: validate user + return JWT token
    @PostMapping("/login")
    public Map<String, String> login(@RequestBody User user) {
        User existing = userRepository.findByUsername(user.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!encoder.matches(user.getPassword(), existing.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        String token = jwtUtil.generateToken(existing.getUsername());
        return Map.of("token", token);
    }
}
