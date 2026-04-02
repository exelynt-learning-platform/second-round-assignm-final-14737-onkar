package com.ecommerce.project.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecommerce.project.entity.CartItem;
import com.ecommerce.project.entity.Order;
import com.ecommerce.project.entity.OrderItem;
import com.ecommerce.project.entity.OrderStatus;
import com.ecommerce.project.entity.Product;
import com.ecommerce.project.entity.User;
import com.ecommerce.project.repository.CartRepository;
import com.ecommerce.project.repository.OrderRepository;
import com.ecommerce.project.repository.ProductRepository;

import java.util.*;

@Service
public class OrderService {

    @Autowired
    private CartRepository cartRepo;

    @Autowired
    private OrderRepository orderRepo;

    @Autowired
    private ProductRepository productRepo;

    // ✅ PLACE ORDER FROM CART (TRANSACTION SAFE)
    @Transactional(rollbackFor = Exception.class)
    public Order placeOrder(User user, String address) {

        // ✅ User validation
        if (user == null || user.getId() == null) {
            throw new RuntimeException("Valid user required");
        }

        List<CartItem> cartItems = cartRepo.findByUserId(user.getId());

        if (cartItems == null || cartItems.isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        List<OrderItem> orderItems = new ArrayList<>();
        double total = 0;

        for (CartItem cart : cartItems) {

            if (cart == null) {
                throw new RuntimeException("Cart item is null");
            }

            if (cart.getProduct() == null) {
                throw new RuntimeException("Product reference missing in cart");
            }

            if (cart.getProduct().getId() == null) {
                throw new RuntimeException("Product ID missing in cart");
            }

            // Fetch latest product
            Product product = productRepo.findById(cart.getProduct().getId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            if (product.getStock() < cart.getQuantity()) {
                throw new RuntimeException("Insufficient stock for " + product.getName());
            }

            product.setStock(product.getStock() - cart.getQuantity());
            productRepo.save(product);

            OrderItem item = new OrderItem();
            item.setProductName(product.getName());
            item.setQuantity(cart.getQuantity());
            item.setPrice(product.getPrice());

            total += cart.getQuantity() * product.getPrice();
            orderItems.add(item);
        }

        // ✅ Create order
        Order order = new Order();
        order.setUser(user);
        order.setItems(orderItems);
        order.setTotalAmount(total);
        order.setAddress(address);
        order.setStatus(OrderStatus.CREATED);

        // ✅ Clear cart
        cartRepo.deleteAll(cartItems);

        // ✅ Save order (if this fails → everything rolls back)
        return orderRepo.save(order);
    }

    // ✅ GET USER ORDERS
    @Transactional(readOnly = true)
    public List<Order> getOrders(User user) {

        if (user == null || user.getId() == null) {
            throw new RuntimeException("Valid user required");
        }

        return orderRepo.findByUserId(user.getId());
    }
}