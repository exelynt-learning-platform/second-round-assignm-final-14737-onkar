package com.ecommerce.project.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecommerce.project.entity.CartItem;
import com.ecommerce.project.entity.Product;

public interface CartRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByUserId(Long userId);
    Optional<CartItem> findByIdAndUserId(Long id, Long userId);
    
    
    
}
