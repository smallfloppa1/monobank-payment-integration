package com.example.monobankpayment.controller;

import com.example.monobankpayment.controller.dto.handlewebhook.WebhookRequest;
import com.example.monobankpayment.controller.dto.preparepayment.PreparePaymentRequest;
import com.example.monobankpayment.controller.dto.preparepayment.PreparePaymentResponse;
import com.example.monobankpayment.service.MonobankService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/monobank")
public class MonobankController {

    private final MonobankService monobankService;

    @PostMapping("/prepare-payment")
    public ResponseEntity<PreparePaymentResponse> preparePayment(@RequestBody PreparePaymentRequest request) {
        return ResponseEntity.ok(monobankService.preparePayment(request));
    }

    @PostMapping("/webhook")
    public ResponseEntity<Void> handleWebhook(@RequestBody WebhookRequest request) {
        monobankService.handleWebhook(request);
        return ResponseEntity.ok().build();

    }
}
