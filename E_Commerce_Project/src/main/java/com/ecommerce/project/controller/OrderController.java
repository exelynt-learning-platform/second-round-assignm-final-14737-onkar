package com.ecommerce.project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.ecommerce.project.dto.OrderRequest;
import com.ecommerce.project.entity.Order;
import com.ecommerce.project.entity.User;
import com.ecommerce.project.service.OrderService;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService service;

    // ✅ PLACE ORDER
    @PostMapping
    public Order placeOrder(@RequestBody OrderRequest request,
                            @AuthenticationPrincipal User user) {

        return service.placeOrder(user, request.getAddress());
    }

    // ✅ GET ORDERS
    @GetMapping
    public List<Order> getOrders(@AuthenticationPrincipal User user) {

        return service.getOrders(user);
    }
}