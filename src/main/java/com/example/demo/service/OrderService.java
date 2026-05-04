package com.example.demo.service;

import com.example.demo.controller.OrderRequest;
import com.example.demo.entity.Order;
import com.example.demo.entity.OrderItem;
import com.example.demo.entity.Product;
import com.example.demo.repository.OrderItemRepository;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    @Transactional
    public Order createOrder(OrderRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("商品が存在しません"));

        if (product.getStock() < request.getQuantity()) {
            throw new RuntimeException("在庫が不足しています");
        }

        product.setStock(product.getStock() - request.getQuantity());
        productRepository.save(product);

        int totalPrice = product.getPrice() * request.getQuantity();

        Order order = new Order();
        order.setTotalPrice(totalPrice);
        Order savedOrder = orderRepository.save(order);

        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(savedOrder);
        orderItem.setProduct(product);
        orderItem.setQuantity(request.getQuantity());
        orderItem.setPrice(product.getPrice());

        orderItemRepository.save(orderItem);

        return savedOrder;
    }

    public java.util.List<Order> findAll() {
        return orderRepository.findAll();
    }
}
