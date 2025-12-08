package com.example.siparis.service;


import com.example.siparis.dto.CreateOrderRequest;
import com.example.siparis.dto.OrderItemRequest;
import com.example.siparis.dto.UpdateOrderStatusRequest;
import com.example.siparis.dto.OrderSummary;      // ⭐ BUNU EKLE
import com.example.siparis.dto.OrderItemResponse; // ⭐ BUNU EKLE
import com.example.siparis.entity.Order;
import com.example.siparis.entity.OrderItem;
import com.example.siparis.entity.OrderStatus;
import com.example.siparis.repository.OrderItemRepository;
import com.example.siparis.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    public OrderService(OrderRepository orderRepository,
                        OrderItemRepository orderItemRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
    }

    public Order createOrder(CreateOrderRequest request) {

        // ⭐ 1) Order oluştur
        Order order = new Order();
        order.setCustomerName(request.getCustomerName());
        order.setCustomerEmail(request.getCustomerEmail());
        order.setCustomerAddress(request.getCustomerAddress());
        order.setStatus(OrderStatus.NEW);

        // ⭐ 2) Müşteri özel notu EKLENDİ
        order.setNote(request.getNote());

        // ⭐ 3) Item listesi oluştur
        List<OrderItem> items = new ArrayList<>();

        if (request.getItems() != null) {
            for (OrderItemRequest itemRequest : request.getItems()) {

                OrderItem item = new OrderItem();
                item.setProductName(itemRequest.getProductName());
                item.setQuantity(itemRequest.getQuantity());
                item.setUnitPrice(itemRequest.getUnitPrice());

                // Order ile ilişkiyi kur
                item.setOrder(order);

                items.add(item);
            }
        }

        // Order içine set et
        order.setItems(items);

        // ⭐ 4) Total fiyatı hesapla
        order.calculateTotalPrice();

        // ⭐ 5) Order kaydedilince item'lar otomatik kaydolacak (cascade ALL)
        return orderRepository.save(order);
    }
    public OrderSummary getOrderSummary(Long id) {
        Order order = getOrder(id); // ⭐ GEREKLİ SATIR

        OrderSummary summary = new OrderSummary();
        summary.setId(order.getId());
        summary.setCustomerName(order.getCustomerName());
        summary.setCustomerEmail(order.getCustomerEmail());
        summary.setCustomerAddress(order.getCustomerAddress());
        summary.setNote(order.getNote());
        summary.setTotalPrice(order.getTotalPrice());

        List<OrderItemResponse> itemResponses = new ArrayList<>();

        for (OrderItem item : order.getItems()) {
            OrderItemResponse i = new OrderItemResponse();
            i.setProductName(item.getProductName());
            i.setQuantity(item.getQuantity());
            i.setUnitPrice(item.getUnitPrice());
            i.setLineTotal(item.getLineTotal());

            itemResponses.add(i);
        }

        summary.setItems(itemResponses);

        return summary;
    }


    public List<Order> getAllOrders(OrderStatus status) {
        if (status != null) {
            return orderRepository.findByStatus(status);
        }
        return orderRepository.findAll();
    }

    public Order getOrder(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }

    public Order updateStatus(Long id, UpdateOrderStatusRequest request) {
        Order order = getOrder(id);
        OrderStatus newStatus = OrderStatus.valueOf(request.getStatus());
        order.setStatus(newStatus);
        return orderRepository.save(order);
    }
}
