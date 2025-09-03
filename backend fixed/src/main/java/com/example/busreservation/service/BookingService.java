package com.example.busreservation.service;

import com.example.busreservation.dto.*;
import com.example.busreservation.model.*;
import com.example.busreservation.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class BookingService {
    @Autowired
    private BookingRepository bookingRepo;
    @Autowired
    private BookingSeatRepository bookingSeatRepo;
    @Autowired
    private SeatRepository seatRepo;
    @Autowired
    private TripRepository tripRepo;
    @Autowired
    private UserRepository userRepo;

    @Transactional
    public Booking hold(Long userId, HoldRequest req) {
        Trip trip = tripRepo.findById(req.getTripId()).orElseThrow();
        User user = userRepo.findById(userId).orElseThrow();

        Booking b = new Booking();
        b.setTrip(trip);
        b.setUser(user);
        b.setStatus("HOLD");
        double amount = 0.0;
        for (Long sid : req.getSeatIds()) {
            Seat s = seatRepo.findById(sid).orElseThrow();
            if (s.isBooked())
                throw new RuntimeException("Seat already booked");
            BookingSeat bs = new BookingSeat();
            bs.setBooking(b);
            bs.setSeat(s);
            b.getSeats().add(bs);
            amount += (trip.getFare() == null ? 0.0 : trip.getFare());
        }
        b.setTotalAmount(amount);
        return bookingRepo.save(b);
    }

    @Transactional
    public Booking confirm(Long bookingId) {
        Booking b = bookingRepo.findById(bookingId).orElseThrow();
        b.setStatus("CONFIRMED");

        List<BookingSeat> list = bookingSeatRepo.findByBookingId(bookingId);
        for (BookingSeat bs : list) {
            Seat seat = bs.getSeat();
            seat.setBooked(true);
            seatRepo.save(seat);
        }
        return bookingRepo.save(b);
    }

    @Transactional
    public Booking cancel(Long bookingId) {
        Booking b = bookingRepo.findById(bookingId).orElseThrow();
        if ("CONFIRMED".equals(b.getStatus())) {
            List<BookingSeat> list = bookingSeatRepo.findByBookingId(bookingId);
            for (BookingSeat bs : list) {
                Seat seat = bs.getSeat();
                seat.setBooked(false);
                seatRepo.save(seat);
            }
        }
        b.setStatus("CANCELLED");
        return bookingRepo.save(b);
    }
}
