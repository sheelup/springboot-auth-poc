package com.sheelu.spring.auth.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
@ConfigurationProperties(prefix = "app.jwt")
public class JwtSettings {

    private Integer tokenExpirationTime;
    private String tokenIssuer;
    private String tokenSigningKey;
    private Integer refreshTokenExpTime;
}
