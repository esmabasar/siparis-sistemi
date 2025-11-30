package com.example.siparis.controller;

import com.example.siparis.dto.CreateOrderRequest;
import com.example.siparis.dto.OrderItemRequest;
import com.example.siparis.dto.UpdateOrderStatusRequest;
import com.example.siparis.entity.OrderStatus;
import com.example.siparis.service.OrderService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

@Controller
public class OrderPageController {

    private final OrderService orderService;

    // Hazır MENÜ (ürün adı → fiyat)
    private static final Map<String, BigDecimal> MENU = new LinkedHashMap<>();

    static {
        MENU.put("Hamburger Menü", new BigDecimal("120.00"));
        MENU.put("Pizza (Orta)", new BigDecimal("150.00"));
        MENU.put("Kola", new BigDecimal("25.00"));
        MENU.put("Su", new BigDecimal("10.00"));
    }

    public OrderPageController(OrderService orderService) {
        this.orderService = orderService;
    }

    // Tüm siparişleri listeleyen sayfa
    @GetMapping("/orders-page")
    public String listOrders(@RequestParam(required = false) OrderStatus status,
                             Model model) {

        model.addAttribute("orders", orderService.getAllOrders(status));
        model.addAttribute("statuses", OrderStatus.values());
        model.addAttribute("selectedStatus", status);

        return "orders"; // templates/orders.html
    }

    // Yeni sipariş formu sayfası (menüyü sayfaya gönderiyoruz)
    @GetMapping("/new-order")
    public String newOrderForm(Model model) {
        model.addAttribute("menu", MENU);
        return "new-order"; // templates/new-order.html
    }

    // Formdan gelen verilerle TEK ürünlü bir sipariş oluşturuyoruz
    @PostMapping("/new-order")
    public String createOrderFromForm(@RequestParam String customerName,
                                      @RequestParam String customerEmail,
                                      @RequestParam String customerAddress,
                                      @RequestParam String productName,
                                      @RequestParam int quantity) {

        // Ürünün fiyatını menüden bul
        BigDecimal unitPrice = MENU.get(productName);
        if (unitPrice == null) {
            unitPrice = BigDecimal.ZERO; // normalde olmamalı, ama tedbir
        }

        CreateOrderRequest request = new CreateOrderRequest();
        request.setCustomerName(customerName);
        request.setCustomerEmail(customerEmail);
        request.setCustomerAddress(customerAddress);

        OrderItemRequest item = new OrderItemRequest();
        item.setProductName(productName);
        item.setQuantity(quantity);
        item.setUnitPrice(unitPrice);

        request.setItems(Collections.singletonList(item));

        orderService.createOrder(request);

        return "redirect:/orders-page";
    }

    // Sipariş durumunu sayfadan güncelleyen endpoint
    @PostMapping("/orders-page/{id}/status")
    public String updateStatusFromPage(@PathVariable Long id,
                                       @RequestParam String status) {

        UpdateOrderStatusRequest request = new UpdateOrderStatusRequest();
        request.setStatus(status);
        orderService.updateStatus(id, request);

        return "redirect:/orders-page";
    }
}
