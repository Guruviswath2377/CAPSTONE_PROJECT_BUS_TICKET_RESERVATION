package com.example.busreservation.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.example.busreservation.dto.*;
import com.example.busreservation.model.Booking;
import com.example.busreservation.model.User;
import com.example.busreservation.repository.UserRepository;
import com.example.busreservation.service.BookingService;
import org.springframework.security.access.prepost.PreAuthorize;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/v1")
@CrossOrigin
public class BookingController {

    @Autowired
    private BookingService bookingService;
    @Autowired
    private UserRepository userRepo;

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @PostMapping("/bookings/hold")
    public BookingResponse hold(@RequestBody HoldRequest req, Authentication auth) {
        String email = auth.getName();
        User u = userRepo.findByEmail(email).orElseThrow();
        Booking b = bookingService.hold(u.getId(), req);

        BookingResponse r = new BookingResponse();
        r.setBookingId(b.getId());
        r.setStatus(b.getStatus());
        r.setAmount(b.getTotalAmount());
        return r;
    }

    @Operation(summary = "Cancel a booking", parameters = @Parameter(name = "id", in = ParameterIn.PATH, required = true, description = "Booking ID to cancel"))
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @PostMapping("/bookings/{id}/cancel")
    public BookingResponse cancel(@PathVariable("id") Long id, // ← name it explicitly
            @RequestBody(required = false) CancelRequest req) {
        Booking b = bookingService.cancel(id);

        BookingResponse r = new BookingResponse();
        r.setBookingId(b.getId());
        r.setStatus(b.getStatus());
        r.setAmount(b.getTotalAmount());
        return r;
    }
}
