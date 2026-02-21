package com.example.monobankpayment.utils;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.example.monobankpayment.client.MonobankClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class MonobankPublicKeyProvider {

    private static final String KEY_NAME = "MONOBANK_PUBLIC_KEY";

    private final MonobankClient client;

    private final Cache<String, String> cache = Caffeine.newBuilder()
            .expireAfterWrite(Duration.ofHours(2))
            .maximumSize(1)
            .build();

    public String provide() {
        return cache.get(KEY_NAME, key -> client.fetchMonobankPublicKey());
    }
}
