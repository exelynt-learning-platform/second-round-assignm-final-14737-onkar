package com.ecommerce.project.service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.ecommerce.project.entity.Order;
import com.ecommerce.project.entity.OrderStatus;
import com.ecommerce.project.exception.PaymentException;
import com.ecommerce.project.repository.OrderRepository;

import jakarta.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

@Service
public class PaymentService {

    private static final Logger logger = Logger.getLogger(PaymentService.class.getName());

    @Value("${stripe.secret.key}")
    private String stripeKey;

    @Autowired
    private OrderRepository orderRepository;

    // ✅ Initialize Stripe API key safely
    @PostConstruct
    public void init() {
        if (stripeKey == null || stripeKey.trim().isEmpty()) {
            logger.severe("Stripe key not configured. Please set stripe.secret.key in application.properties!");
            throw new IllegalStateException("Stripe key not configured");
        }

        try {
            Stripe.apiKey = stripeKey;
            // Optional: verify key format
            if (!stripeKey.startsWith("sk_")) {
                logger.warning("Stripe key does not look like a valid secret key. Please check your configuration.");
            }
            logger.info("Stripe API initialized successfully.");
        } catch (Exception e) {
            logger.severe("Failed to initialize Stripe API: " + e.getMessage());
            throw new IllegalStateException("Stripe initialization failed", e);
        }
    }

    // ✅ Process payment linked with order
    public String pay(Long orderId) {

        if (orderId == null) {
            throw new PaymentException("Order ID is required");
        }

        // ✅ Fetch order
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new PaymentException("Order not found"));

        if (order.getTotalAmount() == null || order.getTotalAmount() <= 0) {
            throw new PaymentException("Invalid order amount");
        }

        try {
            // ✅ Stripe payment parameters
            Map<String, Object> params = new HashMap<>();
            params.put("amount", (int) (order.getTotalAmount() * 100)); // in smallest currency unit
            params.put("currency", "inr");

            PaymentIntent intent = PaymentIntent.create(params);

            // ✅ Update order status
            order.setStatus(OrderStatus.PAID);
            orderRepository.save(order);

            logger.info("Payment successful for orderId: " + orderId + ", PaymentIntentId: " + intent.getId());

            return intent.getId();

        } catch (StripeException e) {

            order.setStatus(OrderStatus.FAILED);
            orderRepository.save(order);

            logger.severe("Payment failed for orderId: " + orderId + ", reason: " + e.getMessage());

            throw new PaymentException("Payment failed: " + e.getMessage());
        }
    }
}