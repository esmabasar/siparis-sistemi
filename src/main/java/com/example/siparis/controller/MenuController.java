package com.example.siparis.controller;

import com.example.siparis.dto.MenuItem;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/menu")
public class MenuController {

    @GetMapping
    public List<MenuItem> getMenu() {
        return List.of(
                new MenuItem("Hamburger", new BigDecimal("120")),
                new MenuItem("Kola", new BigDecimal("30")),
                new MenuItem("Patates Kızartması", new BigDecimal("50"))
        );
    }
}
