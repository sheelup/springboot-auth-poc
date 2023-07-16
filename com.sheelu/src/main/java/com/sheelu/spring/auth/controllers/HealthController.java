package com.sheelu.spring.auth.controllers;

import com.sheelu.spring.auth.GlobalConstants;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {
    @GetMapping(value = GlobalConstants.HEALTH_API_V1_API)
    public String health() {
        return "Hi! I am doing great";
    }
}
