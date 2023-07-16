package com.sheelu.spring.auth.controllers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class HealthControllerTest {

    @Test
    public void testHealth() {
        HealthController healthController = new HealthController();
        Assertions.assertEquals("Hi! I am doing great", healthController.health());
    }
}
