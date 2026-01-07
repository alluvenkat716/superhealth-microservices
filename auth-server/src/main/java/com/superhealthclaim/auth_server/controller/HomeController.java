package com.superhealthclaim.auth_server.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "Auth Server is running";
    }

    @GetMapping("/health")
    public String health() {
        return "UP";
    }
}
