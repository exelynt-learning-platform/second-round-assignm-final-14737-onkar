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
import com.ecommerce.project.exception.OrderException;
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
            throw new OrderException("Valid user required");
        }

        List<CartItem> cartItems = cartRepo.findByUserId(user.getId());

        if (cartItems == null || cartItems.isEmpty()) {
            throw new OrderException("Cart is empty");
        }

        List<OrderItem> orderItems = new ArrayList<>();
        double total = 0;

        for (CartItem cart : cartItems) {

            if (cart == null) {
                throw new OrderException("Cart item is null");
            }

            if (cart.getProduct() == null) {
                throw new OrderException("Product reference missing in cart");
            }

            if (cart.getProduct().getId() == null) {
                throw new OrderException("Product ID missing in cart");
            }

            // ✅ Fetch latest product
            Product product = productRepo.findById(cart.getProduct().getId())
                    .orElseThrow(() -> new OrderException("Product not found"));

            // ✅ Stock validation
            if (product.getStock() < cart.getQuantity()) {
                throw new OrderException("Insufficient stock for " + product.getName());
            }

            // ✅ Reduce stock
            product.setStock(product.getStock() - cart.getQuantity());
            productRepo.save(product);

            // ✅ Create order item
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

        // ✅ Save order
        return orderRepo.save(order);
    }

    // ✅ GET USER ORDERS
    @Transactional(readOnly = true)
    public List<Order> getOrders(User user) {

        if (user == null || user.getId() == null) {
            throw new OrderException("Valid user required");
        }

        return orderRepo.findByUserId(user.getId());
    }
}