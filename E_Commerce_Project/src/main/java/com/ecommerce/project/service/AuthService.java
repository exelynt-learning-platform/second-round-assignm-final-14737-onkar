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

    // ✅ REGISTER
    public String register(User user) {

        // ✅ Duplicate user check
        Optional<User> existing = repo.findByEmail(user.getEmail());
        if (existing.isPresent()) {
            throw new RuntimeException("User already exists");
        }

        // ✅ Password validation (keep simple, no break)
        if (user.getPassword() == null || user.getPassword().length() < 6) {
            throw new RuntimeException("Password must be at least 6 characters");
        }

        // ✅ Encrypt password
        user.setPassword(encoder.encode(user.getPassword()));

        // ✅ Default role
        user.setRole("ROLE_USER");

        repo.save(user);

        return "User registered successfully";
    }

    // ✅ LOGIN
    public String login(AuthRequest req) {

        if (req.getEmail() == null || req.getPassword() == null) {
            throw new RuntimeException("Email and password are required");
        }

        User user = repo.findByEmail(req.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!encoder.matches(req.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        return jwtUtil.generateToken(user.getEmail());
    }
}