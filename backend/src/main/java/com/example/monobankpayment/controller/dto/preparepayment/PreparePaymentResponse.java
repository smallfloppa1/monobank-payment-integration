package com.example.monobankpayment.controller.dto.preparepayment;

public record PreparePaymentResponse(
        String keyId,
        String signature,
        String requestId,
        String payloadBase64
) {
}
