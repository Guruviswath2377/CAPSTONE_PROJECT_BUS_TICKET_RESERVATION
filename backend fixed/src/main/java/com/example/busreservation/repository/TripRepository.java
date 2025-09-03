package com.example.busreservation.repository;

import java.time.Instant;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.example.busreservation.model.Trip;

public interface TripRepository extends JpaRepository<Trip, Long> {

        @Query("select t from Trip t " +
                        "where lower(t.route.source) = lower(:source) " +
                        "  and lower(t.route.destination) = lower(:destination) " +
                        "  and t.departureTime >= :from " +
                        "  and t.departureTime <  :to")
        List<Trip> searchTrips(@Param("source") String source,
                        @Param("destination") String destination,
                        @Param("from") Instant from,
                        @Param("to") Instant to);
}
