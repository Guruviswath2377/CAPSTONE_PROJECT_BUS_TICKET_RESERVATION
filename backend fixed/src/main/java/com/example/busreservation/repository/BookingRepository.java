package com.example.busreservation.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.busreservation.model.Booking;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByTripId(Long tripId);
}
