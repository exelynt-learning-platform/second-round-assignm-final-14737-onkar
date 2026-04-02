package com.ecommerce.project.controller;
 

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.ecommerce.project.service.PaymentService;

@RestController
@RequestMapping("/payment")
public class PaymentController {

    @Autowired
    private PaymentService service;

    @PostMapping("/{orderId}")
    public String pay(@PathVariable Long orderId) {
        return service.pay(orderId);
    }
}
