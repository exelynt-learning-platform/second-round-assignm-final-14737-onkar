package com.ecommerce.project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.ecommerce.project.entity.CartItem;
import com.ecommerce.project.entity.User;
import com.ecommerce.project.service.CartService;

import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService service;

    // ✅ ADD ITEM
    @PostMapping
    public CartItem add(@RequestBody CartItem item,
                        @AuthenticationPrincipal User user) {

        if (user == null) {
            throw new RuntimeException("User not authenticated");
        }

        item.setUser(user);

        return service.add(item);
    }

    // ✅ GET CART
    @GetMapping
    public List<CartItem> get(@AuthenticationPrincipal User user) {

        if (user == null) {
            throw new RuntimeException("User not authenticated");
        }

        return service.get(user);
    }

    // ✅ DELETE ITEM (SECURE FIX)
    @DeleteMapping("/{id}")
    public String remove(@PathVariable Long id,
                         @AuthenticationPrincipal User user) {

        if (user == null) {
            throw new RuntimeException("User not authenticated");
        }

        //   fetch only if belongs to user
        CartItem item = service.getByIdAndUser(id, user.getId());

        service.remove(item.getId());

        return "Item Removed";
    }
}