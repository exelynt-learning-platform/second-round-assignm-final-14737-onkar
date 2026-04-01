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

        if (product == null) {
            throw new RuntimeException("Product cannot be null");
        }

        return repo.save(product);
    }

    // ✅ GET ALL PRODUCTS
    public List<Product> getAll() {
        return repo.findAll();
    }

    // ✅ UPDATE PRODUCT (NULL-SAFE PARTIAL UPDATE)
    public Product update(Long id, Product product) {

        if (id == null) {
            throw new RuntimeException("Product ID is required");
        }

        if (product == null) {
            throw new RuntimeException("Product data is required");
        }

        // ✅ Fetch existing product
        Product existing = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // ✅ Update only provided fields (NULL SAFE)

        if (product.getName() != null) {
            existing.setName(product.getName());
        }
        if (product.getPrice() != 0.0) {
            existing.setPrice(product.getPrice());
        }

        if (product.getStock() != 0) {
            existing.setStock(product.getStock());
        }

        if (product.getDescription() != null) {
            existing.setDescription(product.getDescription());
        }

        return repo.save(existing);
    }

    // ✅ DELETE PRODUCT
    public void delete(Long id) {

        if (id == null) {
            throw new RuntimeException("Product ID is required");
        }

        repo.deleteById(id);
    }
}