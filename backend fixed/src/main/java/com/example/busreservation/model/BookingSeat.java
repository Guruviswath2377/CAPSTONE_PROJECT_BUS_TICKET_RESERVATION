package com.example.busreservation.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "booking_id", "seat_id" }))
public class BookingSeat {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false) private Booking booking;
    @ManyToOne(optional = false) private Seat seat;
}
