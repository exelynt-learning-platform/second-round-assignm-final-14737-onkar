package com.ecommerce.project.service;
 

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ecommerce.project.entity.CartItem;
import com.ecommerce.project.entity.Order;
import com.ecommerce.project.entity.OrderItem;
import com.ecommerce.project.entity.OrderStatus;
import com.ecommerce.project.entity.Product;
import com.ecommerce.project.entity.User;
import com.ecommerce.project.repository.CartRepository;
import com.ecommerce.project.repository.OrderRepository;
import com.ecommerce.project.repository.ProductRepository;

import jakarta.transaction.Transactional;

import java.util.*;

@Service
 
public class OrderService {

    @Autowired
    private CartRepository cartRepo;

    @Autowired
    private OrderRepository orderRepo;
    
    @Autowired
    private ProductRepository productRepo;
    
    

    // PLACE ORDER FROM CART
    @Transactional
    public Order placeOrder(User user, String address) {

        List<CartItem> cartItems = cartRepo.findByUserId(user.getId());

        if (cartItems.isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        List<OrderItem> orderItems = new ArrayList<>();
        double total = 0;

        for (CartItem cart : cartItems) {

            Product product = cart.getProduct();

            if (product.getStock() < cart.getQuantity()) {
                throw new RuntimeException("Insufficient stock for " + product.getName());
            }

            // ✅ reduce stock
            product.setStock(product.getStock() - cart.getQuantity());
            productRepo.save(product);

            OrderItem item = new OrderItem();
            item.setProductName(product.getName());
            item.setQuantity(cart.getQuantity());
            item.setPrice(product.getPrice());

            total += cart.getQuantity() * product.getPrice();

            orderItems.add(item);
        }

        // ✅ CREATE ORDER
        Order order = new Order();
        order.setUser(user);
        order.setItems(orderItems);
        order.setTotalAmount(total);
        order.setAddress(address);
        order.setStatus(OrderStatus.CREATED);

        // ✅ CLEAR CART
        cartRepo.deleteAll(cartItems);

        // ✅ SAVE & RETURN
        return orderRepo.save(order);
    }

    // GET USER ORDERS
    public List<Order> getOrders(User user) {
        return orderRepo.findByUserId(user.getId());
    }
    
    //to placing the order
	     


}