package com.example.busreservation.controller;

import com.example.busreservation.dto.BookingReportResponse;
import com.example.busreservation.dto.PaymentReportResponse;
import com.example.busreservation.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reports")
@CrossOrigin
public class ReportsController {

    @Autowired
    private ReportService reportService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/bookings")
    public ResponseEntity<List<BookingReportResponse>> getBookingReport() {
        return ResponseEntity.ok(reportService.getBookingReport());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/payments")
    public ResponseEntity<List<PaymentReportResponse>> getPaymentReport() {
        return ResponseEntity.ok(reportService.getPaymentReport());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/bookings/pdf")
    public ResponseEntity<byte[]> downloadBookingReportPdf() throws Exception {
        return reportService.downloadBookingReportPdf();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/payments/pdf")
    public ResponseEntity<byte[]> downloadPaymentReportPdf() throws Exception {
        return reportService.downloadPaymentReportPdf();
    }
}
