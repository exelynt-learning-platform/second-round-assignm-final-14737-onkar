package com.ecommerce.project.dto;

public class RegisterResponse {

    private String message;
    private String email;

    // ✅ NEW constructor (FIX)
    public RegisterResponse(String message, String email) {
        this.message = message;
        this.email = email;
    }

    // ✅ Optional (keep if needed)
    public RegisterResponse(String message) {
        this.message = message;
    }

    // ✅ Getters
    public String getMessage() {
        return message;
    }

    public String getEmail() {
        return email;
    }
}