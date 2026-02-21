package com.example.monobankpayment.client;

import com.example.monobankpayment.client.dto.MonobankFetchPublicKeyResponse;
import com.example.monobankpayment.config.MonobankConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class MonobankClient {

    private final MonobankConfig config;

    private final RestClient restClient = RestClient.create();

    public String fetchMonobankPublicKey() {
        MonobankFetchPublicKeyResponse result = restClient.get()
                .uri("https://api.monobank.ua/api/merchant/pubkey")
                .header("X-Token", config.getXToken())
                .retrieve()
                .body(MonobankFetchPublicKeyResponse.class);

        if (result == null) {
            throw new RuntimeException("Error while fetching Monobank public key");
        }

        return result.key();
    }
}
