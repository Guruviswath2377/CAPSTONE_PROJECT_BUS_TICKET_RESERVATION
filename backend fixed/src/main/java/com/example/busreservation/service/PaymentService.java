package com.example.busreservation.service;

import com.example.busreservation.dto.*;
import com.example.busreservation.model.*;
import com.example.busreservation.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class PaymentService {
    @Autowired private PaymentRepository paymentRepo;
    @Autowired private BookingRepository bookingRepo;
    @Autowired private BookingService bookingService;

    @Transactional
    public Payment checkout(CheckoutRequest req) {
        Booking booking = bookingRepo.findById(req.getBookingId()).orElseThrow();
        Payment p = new Payment();
        p.setBooking(booking);
        p.setAmount(booking.getTotalAmount());
        p.setStatus("SUCCESS");
        p.setGatewayRef("DEMO-" + UUID.randomUUID());
        paymentRepo.save(p);
        bookingService.confirm(booking.getId());
        return p;
    }
}
