package com.example.demo.controller;

import com.example.demo.entity.Order;
import com.example.demo.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public Order create(@RequestBody OrderRequest request) {
        return orderService.createOrder(request);
    }

    @GetMapping
    public java.util.List<Order> getAll() {
        return orderService.findAll();
    }
}
