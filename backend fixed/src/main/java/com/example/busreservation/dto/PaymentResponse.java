package com.example.busreservation.dto;

import lombok.Data;

@Data
public class PaymentResponse {
    private Long paymentId;
    private String status;
    private String gatewayRef;
}
