package com.example.monobankpayment.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@Getter @Setter
@Configuration
@ConfigurationProperties(prefix = "monobank")
public class MonobankConfig {
    private String xToken;
    private String publicKeyBase64;
    private String keyId;
}
