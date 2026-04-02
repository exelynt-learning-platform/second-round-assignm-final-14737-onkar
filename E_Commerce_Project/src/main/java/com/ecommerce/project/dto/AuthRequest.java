package com.ecommerce.project.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class AuthRequest {

    @NotNull(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotNull(message = "Password is required")
    @Size(min = 4, message = "Password must be at least 4 characters")
    private String password;

    // ✅ Default constructor (required for JSON binding)
    public AuthRequest() {
    }

    // ✅ Parameterized constructor
    public AuthRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    // ✅ Getters & Setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}