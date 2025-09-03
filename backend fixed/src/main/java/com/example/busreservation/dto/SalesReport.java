package com.example.busreservation.dto;

import lombok.Data;

@Data
public class SalesReport {
    private long totalBookings;
    private double totalRevenue;
    private String topRoute;
}
