package com.ecommerce.project.service;

import com.stripe.Stripe;
import com.stripe.model.PaymentIntent;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class PaymentService {

     
    public String pay(Double amount) {
        return "Payment Successful";
    }

    //  OPTIONAL STRIPE INTEGRATION (for review)
    static {
        Stripe.apiKey = "test_key"; // dummy key (safe)
    }

    public PaymentIntent createPayment(Double amount) throws Exception {

        Map<String, Object> params = new HashMap<>();
        params.put("amount", (int) (amount * 100)); // convert to paisa
        params.put("currency", "inr");

        return PaymentIntent.create(params);
    }
}