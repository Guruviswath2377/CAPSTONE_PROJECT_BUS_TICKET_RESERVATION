package com.example.busreservation.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.Instant;

@Entity
@Getter @Setter
public class Trip {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false) private Bus bus;
    @ManyToOne(optional = false) private Route route;

    private Instant departureTime;
    private Instant arrivalTime;

    private Double fare;
}
