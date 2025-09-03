package com.example.busreservation.service;

import com.example.busreservation.dto.SeatsResponse;
import com.example.busreservation.dto.TripCreateRequest;
import com.example.busreservation.dto.TripSearchResponse;
import com.example.busreservation.model.*;
import com.example.busreservation.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TripService {

    @Autowired
    private TripRepository tripRepo;
    @Autowired
    private BusRepository busRepo;
    @Autowired
    private RouteRepository routeRepo;
    @Autowired
    private SeatRepository seatRepo;

    @Transactional
    public Trip createTrip(TripCreateRequest req) {
        Bus bus = busRepo.findById(req.getBusId()).orElseThrow();
        Route route = routeRepo.findById(req.getRouteId()).orElseThrow();

        Trip t = new Trip();
        t.setBus(bus);
        t.setRoute(route);
        t.setDepartureTime(req.getDepartureTime());
        t.setArrivalTime(req.getArrivalTime());
        t.setFare(req.getFare());
        t = tripRepo.save(t);

        int total = (bus.getTotalSeats() == null || bus.getTotalSeats() <= 0) ? 40 : bus.getTotalSeats();
        for (int i = 1; i <= total; i++) {
            Seat s = new Seat();
            s.setTrip(t);
            s.setSeatNumber(String.valueOf(i));
            s.setSeatType((i % 4 == 1 || i % 4 == 0) ? "window" : "aisle");
            s.setBooked(false);
            seatRepo.save(s);
        }
        return t;
    }

    public Trip findByIdOrThrow(Long id) {
        return tripRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Trip not found"));
    }

    public List<TripSearchResponse> search(String source, String destination, LocalDate date) {
        ZoneId zone = ZoneId.systemDefault();
        Instant from = date.atStartOfDay(zone).toInstant();
        Instant to = from.plus(1, ChronoUnit.DAYS);

        List<Trip> trips = tripRepo.searchTrips(source, destination, from, to);

        List<TripSearchResponse> res = new ArrayList<>();
        for (Trip tr : trips) {
            TripSearchResponse r = new TripSearchResponse();
            r.setId(tr.getId());
            r.setBusNumber(tr.getBus().getBusNumber());
            r.setSource(tr.getRoute().getSource());
            r.setDestination(tr.getRoute().getDestination());
            r.setDepartureTime(tr.getDepartureTime());
            r.setArrivalTime(tr.getArrivalTime());
            r.setFare(tr.getFare());
            res.add(r);
        }
        return res;
    }

    public SeatsResponse seats(Long tripId) {
        List<Seat> seats = seatRepo.findByTrip_Id(tripId);
        SeatsResponse resp = new SeatsResponse();
        resp.setTripId(tripId);
        resp.setSeats(seats.stream().map(s -> {
            SeatsResponse.SeatInfo i = new SeatsResponse.SeatInfo();
            i.setId(s.getId());
            i.setSeatNumber(s.getSeatNumber());
            i.setSeatType(s.getSeatType());
            i.setBooked(s.isBooked());
            return i;
        }).collect(Collectors.toList()));
        return resp;
    }

    public List<Trip> listAll() {
        return tripRepo.findAll();
    }
}
