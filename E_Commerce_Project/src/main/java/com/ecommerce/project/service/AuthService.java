package com.ecommerce.project.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ecommerce.project.dto.AuthRequest;
import com.ecommerce.project.entity.User;
import com.ecommerce.project.repository.UserRepository;
import com.ecommerce.project.security.JwtUtil;
import com.ecommerce.project.exception.UserAlreadyExistsException;
import com.ecommerce.project.exception.UserNotFoundException;
import com.ecommerce.project.exception.InvalidCredentialsException;

@Service
public class AuthService {

    @Autowired
    private UserRepository repo;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder encoder;

    // ✅ REGISTER
    public User register(User user) {

        if (user == null) {
            throw new IllegalArgumentException("User data is required");
        }

        Optional<User> existing = repo.findByEmail(user.getEmail());
        if (existing.isPresent()) {
            throw new UserAlreadyExistsException("User already exists with this email");
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
            throw new IllegalArgumentException("Request cannot be null");
        }

        User user = repo.findByEmail(req.getEmail())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (!encoder.matches(req.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        return jwtUtil.generateToken(user.getEmail());
    }
}