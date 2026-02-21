package com.example.monobankpayment.service;

import com.example.monobankpayment.config.MonobankConfig;
import com.example.monobankpayment.controller.dto.handlewebhook.WebhookRequest;
import com.example.monobankpayment.controller.dto.preparepayment.PreparePaymentRequest;
import com.example.monobankpayment.controller.dto.preparepayment.PreparePaymentResponse;
import com.example.monobankpayment.utils.MonobankPublicKeyProvider;
import com.example.monobankpayment.utils.MonobankSignUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MonobankService {

    private final MonobankConfig config;
    private final MonobankSignUtil sign;
    private final MonobankPublicKeyProvider publicKeyProvider;


    public PreparePaymentResponse preparePayment(PreparePaymentRequest request) {
        String requestId = UUID.randomUUID().toString();
        String payload = generateBase64Payload(request.orderId(), request.description());

        String signature;
        try {
            signature = sign.sign(payload.getBytes());
        } catch (Exception e) {
            throw new RuntimeException("Failed to sign payload", e);
        }

        return new PreparePaymentResponse(
                config.getKeyId(),
                signature,
                requestId,
                payload
        );
    }

    public void handleWebhook(WebhookRequest request) {

    }

    private String generateBase64Payload(String orderId, String description) {
        String orderData = "\"" +
                orderId + ": " + description +
                "\"";


        return Base64.getEncoder()
                .encodeToString(orderData.getBytes(StandardCharsets.UTF_8));
    }
}
