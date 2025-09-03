package com.example.busreservation.dto;

import lombok.Data;
import java.util.List;

@Data
public class HoldRequest {
    private Long tripId;
    private List<Long> seatIds;
}
