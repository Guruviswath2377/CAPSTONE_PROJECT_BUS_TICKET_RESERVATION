package com.example.busreservation.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.example.busreservation.dto.*;
import com.example.busreservation.model.Payment;
import com.example.busreservation.service.PaymentService;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin
public class PaymentController {
    @Autowired
    private PaymentService paymentService;

    @PostMapping("/payments/checkout")
    public PaymentResponse checkout(@RequestBody CheckoutRequest req) {
        Payment p = paymentService.checkout(req);
        PaymentResponse r = new PaymentResponse();
        r.setPaymentId(p.getId());
        r.setStatus(p.getStatus());
        r.setGatewayRef(p.getGatewayRef());
        return r;
    }
}
