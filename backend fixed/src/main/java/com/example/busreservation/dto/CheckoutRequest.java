package com.example.busreservation.dto;

import lombok.Data;

@Data
public class CheckoutRequest {
    private Long bookingId;
    private String method;
}
