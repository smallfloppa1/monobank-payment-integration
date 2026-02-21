package com.example.monobankpayment.controller.dto.handlewebhook;

import java.util.List;

public record WebhookEvent(
        String invoiceId,
        String status,
        String failureReason,
        Integer amount,
        Integer ccy,
        Integer finalAmount,
        String createdDate,
        String modifiedDate,
        String reference,
        List<CancelItem> cancelList
) {}
