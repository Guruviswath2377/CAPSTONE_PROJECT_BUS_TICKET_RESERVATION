
package com.example.busreservation.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.busreservation.model.Seat;

public interface SeatRepository extends JpaRepository<Seat, Long> {

    List<Seat> findByTrip_Id(Long tripId);
}
