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

        item.setUser(user);
        return service.add(item);
    }

    // ✅ GET CART
    @GetMapping
    public List<CartItem> get(@AuthenticationPrincipal User user) {
        return service.get(user);
    }

    // ✅ DELETE ITEM
    @DeleteMapping("/{id}")
    public String remove(@PathVariable Long id,
                         @AuthenticationPrincipal User user) {

        // Ensure item belongs to user (security check stays)
        CartItem item = service.getByIdAndUser(id, user.getId());

        service.remove(item.getId());

        return "Item Removed";
    }
}