package com.example.monobankpayment.controller.dto.handlewebhook;

public record CancelItem(
        String status,
        Integer amount,
        Integer ccy,
        String createdDate,
        String modifiedDate,
        String approvalCode,
        String rrn,
        String extRef
) {}
