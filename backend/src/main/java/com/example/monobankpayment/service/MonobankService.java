package com.example.monobankpayment.service;

import com.example.monobankpayment.config.MonobankConfig;
import com.example.monobankpayment.controller.dto.handlewebhook.WebhookEvent;
import com.example.monobankpayment.controller.dto.preparepayment.PreparePaymentRequest;
import com.example.monobankpayment.controller.dto.preparepayment.PreparePaymentResponse;
import com.example.monobankpayment.utils.MonobankPublicKeyProvider;
import com.example.monobankpayment.utils.MonobankSignUtil;
import com.example.monobankpayment.utils.MonobankWebhookValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MonobankService {

    private final MonobankConfig config;
    private final MonobankSignUtil sign;
    private final MonobankPublicKeyProvider publicKeyProvider;
    private final MonobankWebhookValidator webhookValidator;

    private final ObjectMapper objectMapper = new ObjectMapper();


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

    public void handleWebhook(String signature, byte[] rawBody) {

        webhookValidator.validate(
                rawBody,
                signature,
                publicKeyProvider.provide()
        );

        WebhookEvent webhook = objectMapper.readValue(rawBody, WebhookEvent.class);

        // Future handle logic
    }

    private String generateBase64Payload(String orderId, String description) {
        String orderData = "\"" +
                orderId + ": " + description +
                "\"";


        return Base64.getEncoder()
                .encodeToString(orderData.getBytes(StandardCharsets.UTF_8));
    }
}
