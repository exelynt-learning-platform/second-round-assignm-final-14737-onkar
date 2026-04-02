package com.ecommerce.project.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ecommerce.project.entity.Product;
import com.ecommerce.project.repository.ProductRepository;

import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductRepository repo;

    // ✅ ADD PRODUCT
    public Product add(Product product) {
        return repo.save(product);
    }

    // ✅ GET ALL PRODUCTS
    public List<Product> getAll() {
        return repo.findAll();
    }

    // ✅ UPDATE PRODUCT (FIXED - PARTIAL UPDATE SAFE)
    public Product update(Long id, Product product) {

        // ✅ Fetch existing product
        Product existing = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // ✅ Update only provided fields

        if (product.getName() != null) {
            existing.setName(product.getName());
        }

        if (product.getPrice() != 0) {
            existing.setPrice(product.getPrice());
        }

        if (product.getStock() != 0) {
            existing.setStock(product.getStock());
        }

        if (product.getDescription() != null) {   // if you have description field
            existing.setDescription(product.getDescription());
        }

        return repo.save(existing);
    }

    // ✅ DELETE PRODUCT
    public void delete(Long id) {
        repo.deleteById(id);
    }
}