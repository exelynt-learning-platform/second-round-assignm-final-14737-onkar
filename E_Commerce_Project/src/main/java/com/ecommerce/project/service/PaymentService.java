package com.ecommerce.project.service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.ecommerce.project.exception.PaymentException;

import jakarta.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Service
public class PaymentService {

    @Value("${stripe.secret.key}")
    private String stripeKey;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeKey;
    }

    // ✅ SIMPLE PAYMENT  
    public String pay(Double amount) {

        if (amount == null || amount <= 0) {
            throw new PaymentException("Invalid payment amount");
        }

        return "Payment Successful";
    }

    // ✅ STRIPE PAYMENT  
    public PaymentIntent createPayment(Double amount) {

        try {
            if (amount == null || amount <= 0) {
                throw new PaymentException("Invalid payment amount");
            }

            Map<String, Object> params = new HashMap<>();
            params.put("amount", (int) (amount * 100));
            params.put("currency", "inr");

            return PaymentIntent.create(params);

        } catch (StripeException e) {
            throw new PaymentException("Payment failed: " + e.getMessage());
        } catch (Exception e) {
            throw new PaymentException("Unexpected payment error");
        }
    }
}