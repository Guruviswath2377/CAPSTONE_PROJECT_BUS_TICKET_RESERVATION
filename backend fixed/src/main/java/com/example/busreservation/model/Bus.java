package com.example.busreservation.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Bus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String busNumber;

    private String busType;

    private Integer totalSeats;

    private String operatorName;
    @Lob
    private String seatLayout;
}
