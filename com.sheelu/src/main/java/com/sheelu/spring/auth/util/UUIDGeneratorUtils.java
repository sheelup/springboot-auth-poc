package com.sheelu.spring.auth.util;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UUIDGeneratorUtils {
    public String generateUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
