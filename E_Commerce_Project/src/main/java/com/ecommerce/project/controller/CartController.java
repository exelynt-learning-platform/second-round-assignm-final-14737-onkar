package com.ecommerce.project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.ecommerce.project.entity.CartItem;
import com.ecommerce.project.entity.User;
import com.ecommerce.project.repository.UserRepository;
import com.ecommerce.project.service.CartService;

import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService service;

    @Autowired
    private UserRepository userRepository;

    // ✅ HELPER METHOD 
    private User getLoggedInUser(Authentication auth) {
        String email = auth.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // ✅ ADD ITEM
    @PostMapping
    public CartItem add(@RequestBody CartItem item, Authentication auth) {

        User user = getLoggedInUser(auth);

        item.setUser(user);

        return service.add(item);
    }

    // ✅ GET CART
    @GetMapping
    public List<CartItem> get(Authentication auth) {

        User user = getLoggedInUser(auth);

        return service.get(user);
    }

    // ✅ DELETE ITEM 
    @DeleteMapping("/{id}")
    public String remove(@PathVariable Long id, Authentication auth) {

        User user = getLoggedInUser(auth);

        // ✅ Fetch cart item
        CartItem item = service.getById(id);

        // ✅ Ownership check
        if (!item.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized");
        }

        service.remove(id);

        return "Item Removed";
    }
}