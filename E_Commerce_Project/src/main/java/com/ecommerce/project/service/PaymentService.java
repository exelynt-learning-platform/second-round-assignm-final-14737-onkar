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

@Service
public class PaymentService {

    @Value("${stripe.secret.key}")
    private String stripeKey;

    @Autowired
    private OrderRepository orderRepository;

    @PostConstruct
    public void init() {
        if (stripeKey == null || stripeKey.trim().isEmpty()) {
            throw new IllegalStateException("Stripe key not configured");
        }
        Stripe.apiKey = stripeKey;
    }

    // ✅ PAYMENT LINKED WITH ORDER
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
            // ✅ Stripe payment
            Map<String, Object> params = new HashMap<>();
            params.put("amount", (int) (order.getTotalAmount() * 100));
            params.put("currency", "inr");

            PaymentIntent intent = PaymentIntent.create(params);

            // ✅ Update order status
            order.setStatus(OrderStatus.PAID);
            orderRepository.save(order);

            return intent.getId();

        } catch (StripeException e) {

            order.setStatus(OrderStatus.FAILED);
            orderRepository.save(order);

            throw new PaymentException("Payment failed: " + e.getMessage());
        }
    }
}