package com.example.monobankpayment.controller;

import com.example.monobankpayment.controller.dto.preparepayment.PreparePaymentRequest;
import com.example.monobankpayment.controller.dto.preparepayment.PreparePaymentResponse;
import com.example.monobankpayment.service.MonobankService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<Void> handleWebhook(
            @RequestHeader("X-Sign") String signature,
            @RequestBody byte[] rawBody
    ) {
        monobankService.handleWebhook(signature, rawBody);
        return ResponseEntity.ok().build();

    }
}
