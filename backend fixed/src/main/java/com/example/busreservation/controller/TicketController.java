package com.example.busreservation.controller;

import com.example.busreservation.model.Booking;
import com.example.busreservation.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/v1/tickets")
@CrossOrigin
public class TicketController {

    @Autowired
    private TicketService ticketService;

    @GetMapping("/{bookingId}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<Booking> getTicket(@PathVariable("bookingId") Long bookingId,
            Authentication auth) {
        Booking booking = ticketService.getBookingOr404(bookingId);
        if (!ticketService.isAdmin(auth) && !ticketService.isOwner(booking, auth)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not your booking");
        }
        return ResponseEntity.ok(booking);
    }

    @DeleteMapping("/{bookingId}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<String> cancelTicket(@PathVariable("bookingId") Long bookingId,
            Authentication auth) {
        Booking booking = ticketService.getBookingOr404(bookingId);
        if (!ticketService.isAdmin(auth) && !ticketService.isOwner(booking, auth)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not your booking");
        }
        String msg = ticketService.cancelBooking(bookingId);
        return ResponseEntity.ok(msg);
    }

    @GetMapping(value = "/{bookingId}/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<byte[]> downloadTicketPdf(@PathVariable("bookingId") Long bookingId,
            Authentication auth) throws Exception {
        Booking booking = ticketService.getBookingOr404(bookingId);
        if (!ticketService.isAdmin(auth) && !ticketService.isOwner(booking, auth)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not your booking");
        }
        return ticketService.downloadTicket(bookingId);
    }
}
