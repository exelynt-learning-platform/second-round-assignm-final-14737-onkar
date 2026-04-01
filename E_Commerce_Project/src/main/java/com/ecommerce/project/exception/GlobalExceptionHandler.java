package com.ecommerce.project.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // ✅ USER NOT FOUND
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiError> handleUserNotFound(UserNotFoundException ex) {
        return new ResponseEntity<>(
                new ApiError(ex.getMessage(), HttpStatus.NOT_FOUND.value()),
                HttpStatus.NOT_FOUND
        );
    }

    // ✅ PRODUCT NOT FOUND
    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ApiError> handleProductNotFound(ProductNotFoundException ex) {
        return new ResponseEntity<>(
                new ApiError(ex.getMessage(), HttpStatus.NOT_FOUND.value()),
                HttpStatus.NOT_FOUND
        );
    }

    // ✅ CART ERROR
    @ExceptionHandler(CartException.class)
    public ResponseEntity<ApiError> handleCartException(CartException ex) {
        return new ResponseEntity<>(
                new ApiError(ex.getMessage(), HttpStatus.BAD_REQUEST.value()),
                HttpStatus.BAD_REQUEST
        );
    }

    // ✅ ORDER ERROR
    @ExceptionHandler(OrderException.class)
    public ResponseEntity<ApiError> handleOrderException(OrderException ex) {
        return new ResponseEntity<>(
                new ApiError(ex.getMessage(), HttpStatus.BAD_REQUEST.value()),
                HttpStatus.BAD_REQUEST
        );
    }

    // ✅ PAYMENT ERROR (ADD THIS)
    @ExceptionHandler(PaymentException.class)
    public ResponseEntity<ApiError> handlePaymentException(PaymentException ex) {
        return new ResponseEntity<>(
                new ApiError(ex.getMessage(), HttpStatus.BAD_REQUEST.value()),
                HttpStatus.BAD_REQUEST
        );
    }

    // ✅ VALIDATION ERRORS (VERY IMPORTANT)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex) {

        String errorMsg = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(error -> error.getDefaultMessage())
                .orElse("Validation error");

        return new ResponseEntity<>(
                new ApiError(errorMsg, HttpStatus.BAD_REQUEST.value()),
                HttpStatus.BAD_REQUEST
        );
    }

    // ❌ REMOVED RuntimeException handler (IMPORTANT FIX)

    // ✅ FINAL FALLBACK (SAFE)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleException(Exception ex) {
        return new ResponseEntity<>(
                new ApiError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR.value()),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
} 