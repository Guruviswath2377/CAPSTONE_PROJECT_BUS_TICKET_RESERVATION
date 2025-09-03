package com.example.busreservation.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.Instant;

@Entity
@Getter
@Setter
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Booking booking;

    private Double amount = 0.0;

    private String status = "INITIATED";

    private String gatewayRef;

    private Instant createdAt = Instant.now();
}
