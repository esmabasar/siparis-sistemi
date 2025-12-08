package com.example.siparis.dto;

import java.math.BigDecimal;

public class MenuItem {

    private String name;
    private BigDecimal price;

    public MenuItem(String name, BigDecimal price) {
        this.name = name;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getPrice() {
        return price;
    }
}
