package com.ecommerce.project.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ecommerce.project.entity.CartItem;
import com.ecommerce.project.entity.Product;
import com.ecommerce.project.entity.User;
import com.ecommerce.project.repository.CartRepository;
import com.ecommerce.project.repository.ProductRepository;

import java.util.List;

@Service
public class CartService {

    @Autowired
    private CartRepository repo;

    @Autowired
    private ProductRepository productRepository;

    // ✅ ADD TO CART (FINAL FIXED)
    public CartItem add(CartItem item) {

        // ✅ FULL NULL SAFETY (FINAL FIX)
        if (item == null || 
            item.getUser() == null || 
            item.getUser().getId() == null ||
            item.getProduct() == null || 
            item.getProduct().getId() == null) {

            throw new RuntimeException("User and Product are required");
        }

        // ✅ Fetch product from DB
        Long productId = item.getProduct().getId();

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // ✅ Stock validation
        if (product.getStock() < item.getQuantity()) {
            throw new RuntimeException("Not enough stock");
        }

        // ✅ Check existing cart item
        List<CartItem> existingItems = repo.findByUserId(item.getUser().getId());

        for (CartItem existing : existingItems) {
            if (existing.getProduct().getId().equals(product.getId())) {

                existing.setQuantity(existing.getQuantity() + item.getQuantity());
                return repo.save(existing);
            }
        }

        // ✅ Set correct product
        item.setProduct(product);

        return repo.save(item);
    }

    // ✅ GET USER CART
    public List<CartItem> get(User user) {
        return repo.findByUserId(user.getId());
    }

    // ✅ REMOVE ITEM
    public void remove(Long id) {
        repo.deleteById(id);
    }

    // ✅ GET ITEM BY ID
    public CartItem getById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));
    }
}