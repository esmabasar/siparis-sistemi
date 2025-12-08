package com.example.siparis.controller;

import com.example.siparis.dto.CreateOrderRequest;
import com.example.siparis.dto.UpdateOrderStatusRequest;
import com.example.siparis.dto.OrderSummary;
import com.example.siparis.entity.Order;
import com.example.siparis.entity.OrderStatus;
import com.example.siparis.service.OrderService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // Yeni sipariş oluştur
    @PostMapping
    public Order createOrder(@RequestBody CreateOrderRequest request) {
        return orderService.createOrder(request);
    }

    // Tüm siparişleri (isteğe bağlı status filtresiyle) listele
    @GetMapping
    public List<Order> getOrders(@RequestParam(required = false) OrderStatus status) {
        return orderService.getAllOrders(status);
    }

    // Tek bir siparişi getir
    @GetMapping("/{id}")
    public Order getOrder(@PathVariable Long id) {
        return orderService.getOrder(id);
    }

    // ✨ Sipariş özeti (summary) endpoint'i
    @GetMapping("/{id}/summary")
    public OrderSummary getOrderSummary(@PathVariable Long id) {
        return orderService.getOrderSummary(id);
    }

    // Sipariş durumunu güncelle
    @PatchMapping("/{id}/status")
    public Order updateStatus(@PathVariable Long id,
                              @RequestBody UpdateOrderStatusRequest request) {
        return orderService.updateStatus(id, request);
    }
}
