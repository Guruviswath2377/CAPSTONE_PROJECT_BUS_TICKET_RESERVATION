package com.example.busreservation.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.busreservation.model.BookingSeat;

public interface BookingSeatRepository extends JpaRepository<BookingSeat, Long> {
    List<BookingSeat> findByBookingId(Long bookingId);
}
