package com.example.siparis.service;

import com.example.siparis.dto.CreateOrderRequest;
import com.example.siparis.dto.OrderItemRequest;
import com.example.siparis.dto.UpdateOrderStatusRequest;
import com.example.siparis.entity.Order;
import com.example.siparis.entity.OrderItem;
import com.example.siparis.entity.OrderStatus;
import com.example.siparis.repository.OrderItemRepository;
import com.example.siparis.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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
        Order order = new Order();
        order.setCustomerName(request.getCustomerName());
        order.setCustomerEmail(request.getCustomerEmail());
        order.setCustomerAddress(request.getCustomerAddress());
        order.setStatus(OrderStatus.NEW);

        List<OrderItem> items = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        if (request.getItems() != null) {
            for (OrderItemRequest itemRequest : request.getItems()) {
                OrderItem item = new OrderItem();
                item.setProductName(itemRequest.getProductName());
                item.setQuantity(itemRequest.getQuantity());
                item.setUnitPrice(itemRequest.getUnitPrice());
                item.setOrder(order);

                items.add(item);

                BigDecimal lineTotal = itemRequest.getUnitPrice()
                        .multiply(BigDecimal.valueOf(itemRequest.getQuantity()));
                total = total.add(lineTotal);
            }
        }

        order.setItems(items);
        order.setTotalPrice(total);

        // order kaydedilince item'lar da cascade ile kaydolacak
        return orderRepository.save(order);
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
