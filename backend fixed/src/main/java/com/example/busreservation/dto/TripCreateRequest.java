package com.example.busreservation.dto;

import lombok.Data;
import java.time.Instant;

@Data
public class TripCreateRequest {
    private Long busId;
    private Long routeId;
    private Instant departureTime;
    private Instant arrivalTime;
    private Double fare;
}
