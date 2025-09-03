package com.example.busreservation.dto;

import lombok.Data;
import java.time.Instant;

@Data
public class TripSearchResponse {
    private Long id;
    private String busNumber;
    private String source;
    private String destination;
    private Instant departureTime;
    private Instant arrivalTime;
    private Double fare;
}
