package com.example.monobankpayment.controller.dto.preparepayment;

public record PreparePaymentRequest(
        String orderId,
        Integer amount,
        String description
) {
}
