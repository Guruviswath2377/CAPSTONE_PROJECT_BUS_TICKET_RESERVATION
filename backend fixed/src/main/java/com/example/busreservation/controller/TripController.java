package com.example.busreservation.controller;

import com.example.busreservation.dto.SeatsResponse;
import com.example.busreservation.dto.TripCreateRequest;
import com.example.busreservation.dto.TripSearchResponse;
import com.example.busreservation.model.Trip;
import com.example.busreservation.service.TripService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin
public class TripController {

    @Autowired
    private TripService tripService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/trips")
    public Trip create(@RequestBody TripCreateRequest req) {
        return tripService.createTrip(req);
    }

    @GetMapping("/trips/search")
    @Operation(summary = "Search trips (public)", security = {})
    public List<TripSearchResponse> search(
            @RequestParam(name = "source") String source,
            @RequestParam(name = "destination") String destination,
            @RequestParam(name = "date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return tripService.search(source, destination, date);
    }

    @GetMapping("/trips/{id}")
    @Operation(summary = "Get trip by id (public)", security = {})
    public TripSearchResponse getById(@PathVariable Long id) {
        Trip t = tripService.findByIdOrThrow(id);
        TripSearchResponse r = new TripSearchResponse();
        r.setId(t.getId());
        r.setBusNumber(t.getBus().getBusNumber());
        r.setSource(t.getRoute().getSource());
        r.setDestination(t.getRoute().getDestination());
        r.setDepartureTime(t.getDepartureTime());
        r.setArrivalTime(t.getArrivalTime());
        r.setFare(t.getFare());
        return r;
    }

    @GetMapping("/trips/{id}/seats")
    @Operation(summary = "Seat map for a trip (public)", security = {})
    public SeatsResponse seats(@PathVariable(name = "id") Long id) {
        return tripService.seats(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/trips")
    public List<Trip> listTrips() {
        return tripService.listAll();
    }
}
