package com.example.busreservation.dto;

import lombok.Data;
import java.util.List;

@Data
public class SeatsResponse {
    private Long tripId;
    private List<SeatInfo> seats;

    @Data
    public static class SeatInfo {
        private Long id;
        private String seatNumber;
        private String seatType;
        private boolean booked;
    }
}
