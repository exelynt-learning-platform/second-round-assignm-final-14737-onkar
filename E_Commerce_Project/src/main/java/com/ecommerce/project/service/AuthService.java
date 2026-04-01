package com.ecommerce.project.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ecommerce.project.dto.AuthRequest;
import com.ecommerce.project.entity.User;
import com.ecommerce.project.repository.UserRepository;
import com.ecommerce.project.security.JwtUtil;

@Service
public class AuthService {

    @Autowired
    private UserRepository repo;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder encoder;

    // ✅ REGISTER (VALIDATION REMOVED - handled by @Valid)
    public User register(User user) {

        if (user == null) {
            throw new RuntimeException("User data is required");
        }

        Optional<User> existing = repo.findByEmail(user.getEmail());
        if (existing.isPresent()) {
            throw new RuntimeException("User already exists");
        }

        // Encrypt password
        user.setPassword(encoder.encode(user.getPassword()));

        // Default role
        user.setRole("ROLE_USER");

        return repo.save(user);
    }

    // ✅ LOGIN
    public String login(AuthRequest req) {

        if (req == null) {
            throw new RuntimeException("Request cannot be null");
        }

        

        User user = repo.findByEmail(req.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!encoder.matches(req.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        return jwtUtil.generateToken(user.getEmail());
    }
}