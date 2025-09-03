package com.example.busreservation.service;

import com.example.busreservation.dto.SeatsResponse;
import com.example.busreservation.dto.TripCreateRequest;
import com.example.busreservation.model.*;
import com.example.busreservation.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TripServiceTest {

    @Mock
    TripRepository tripRepo;
    @Mock
    BusRepository busRepo;
    @Mock
    RouteRepository routeRepo;
    @Mock
    SeatRepository seatRepo;

    @InjectMocks
    TripService tripService;

    @Test
    void createTrip_createsSeats_andAssignsTypes() {
        Bus bus = new Bus();
        bus.setId(1L);
        bus.setTotalSeats(8);
        bus.setBusNumber("BN");
        Route route = new Route();
        route.setId(2L);
        route.setSource("S");
        route.setDestination("D");

        Trip saved = new Trip();
        saved.setId(100L);
        saved.setBus(bus);
        saved.setRoute(route);

        when(busRepo.findById(1L)).thenReturn(Optional.of(bus));
        when(routeRepo.findById(2L)).thenReturn(Optional.of(route));
        when(tripRepo.save(any(Trip.class))).thenReturn(saved);

        TripCreateRequest req = new TripCreateRequest();
        req.setBusId(1L);
        req.setRouteId(2L);
        req.setDepartureTime(Instant.now());
        req.setArrivalTime(Instant.now().plusSeconds(3600));
        req.setFare(300.0);

        Trip t = tripService.createTrip(req);

        assertThat(t.getId()).isEqualTo(100L);
        verify(seatRepo, times(8)).save(any(Seat.class));
    }

    @Test
    void seats_returnsMappedDto() {
        Trip trip = new Trip();
        trip.setId(55L);

        List<Seat> list = new ArrayList<>();
        for (int i = 1; i <= 4; i++) {
            Seat s = new Seat();
            s.setId((long) i);
            s.setTrip(trip);
            s.setSeatNumber(String.valueOf(i));
            s.setSeatType(i % 2 == 0 ? "aisle" : "window");
            s.setBooked(i <= 2);
            list.add(s);
        }

        when(seatRepo.findByTrip_Id(55L)).thenReturn(list);

        SeatsResponse resp = tripService.seats(55L);
        assertThat(resp.getTripId()).isEqualTo(55L);
        assertThat(resp.getSeats()).hasSize(4);
        assertThat(resp.getSeats().get(0).getSeatNumber()).isEqualTo("1");
        assertThat(resp.getSeats().get(1).isBooked()).isTrue();
    }
}
