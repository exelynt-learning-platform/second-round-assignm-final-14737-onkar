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

    // ADD TO CART
    public CartItem add(CartItem item) {

        // ✅ Null safety
        if (item.getProduct() == null || item.getProduct().getId() == null) {
            throw new RuntimeException("Product is required");
        }

        // ✅ Fetch product from DB
        Long productId = item.getProduct().getId();

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // ✅ Stock validation
        if (product.getStock() < item.getQuantity()) {
            throw new RuntimeException("Not enough stock");
        }

        // ✅ Set correct product
        item.setProduct(product);

        return repo.save(item);
    }

    // GET USER CART
    public List<CartItem> get(User user) {
        return repo.findByUserId(user.getId());
    }

    // REMOVE ITEM
    public void remove(Long id) {
        repo.deleteById(id);
    }
}