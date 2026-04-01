package com.ecommerce.project.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ecommerce.project.entity.CartItem;
import com.ecommerce.project.entity.Product;
import com.ecommerce.project.entity.User;
import com.ecommerce.project.exception.CartException;
import com.ecommerce.project.repository.CartRepository;
import com.ecommerce.project.repository.ProductRepository;

import java.util.List;

@Service
public class CartService {

    @Autowired
    private CartRepository repo;

    @Autowired
    private ProductRepository productRepository;

    // ✅ ADD TO CART  
    public CartItem add(CartItem item) {

        // ✅ Proper validation  
        if (item == null) {
            throw new RuntimeException("Cart item cannot be null");
        }

        if (item.getUser() == null || item.getUser().getId() == null) {
            throw new RuntimeException("Valid user is required");
        }

        if (item.getProduct() == null || item.getProduct().getId() == null) {
            throw new RuntimeException("Valid product is required");
        }

        if (item.getQuantity() <= 0) {
            throw new RuntimeException("Quantity must be greater than 0");
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

            // ✅ Extra safety check
            if (existing.getProduct() != null &&
                existing.getProduct().getId() != null &&
                existing.getProduct().getId().equals(product.getId())) {

                existing.setQuantity(existing.getQuantity() + item.getQuantity());
                return repo.save(existing);
            }
        }

        // ✅ Set correct product reference
        item.setProduct(product);

        return repo.save(item);
    }

    // ✅ GET USER CART (SAFE)
    public List<CartItem> get(User user) {

        if (user == null || user.getId() == null) {
        	throw new CartException("Valid user is required");
            
        }

        return repo.findByUserId(user.getId());
    }

    // ✅ REMOVE ITEM
    public void remove(Long id) {

        if (id == null) {
        	throw new CartException("Cart item ID is required");
        }

        repo.deleteById(id);
    }

    // ✅ GET ITEM BY ID
    public CartItem getById(Long id) {

        if (id == null) {
            throw new RuntimeException("Cart item ID is required");
        }

        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));
    }
    
    public CartItem getByIdAndUser(Long id, Long userId) {

        if (id == null || userId == null) {
            throw new RuntimeException("Invalid cart item or user");
        }

        return repo.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new RuntimeException("Cart item not found or unauthorized"));
    }
}