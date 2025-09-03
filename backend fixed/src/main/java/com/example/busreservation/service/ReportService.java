package com.example.busreservation.service;

import com.example.busreservation.dto.BookingReportResponse;
import com.example.busreservation.dto.PaymentReportResponse;
import com.example.busreservation.model.Booking;
import com.example.busreservation.model.Payment;
import com.example.busreservation.repository.BookingRepository;
import com.example.busreservation.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

@Service
public class ReportService {

    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private PaymentRepository paymentRepository;

    public List<BookingReportResponse> getBookingReport() {
        return bookingRepository.findAll().stream().map(b -> {
            String passenger = "";
            if (b.getUser() != null) {
                passenger = firstNonBlank(b.getUser().getName(), b.getUser().getEmail());
            }

            String fromCity = "";
            String toCity = "";
            String busNumber = "";
            String busType = "";

            if (b.getTrip() != null) {
                if (b.getTrip().getRoute() != null) {
                    fromCity = safe(b.getTrip().getRoute().getSource());
                    toCity = safe(b.getTrip().getRoute().getDestination());
                }
                if (b.getTrip().getBus() != null) {
                    busNumber = safe(b.getTrip().getBus().getBusNumber());
                    busType = safe(b.getTrip().getBus().getBusType());
                }
            }

            int seatsCount = (b.getSeats() == null) ? 0 : b.getSeats().size();

            String ticketDisplay = "TKT-" + String.format("%06d", b.getId());

            double totalAmt = toDouble(b.getTotalAmount());

            Instant bookingInstant = b.getBookingDate();
            Date bookingDate = (bookingInstant == null) ? null : Date.from(bookingInstant);

            return new BookingReportResponse(
                    b.getId(),
                    ticketDisplay,
                    passenger,
                    fromCity,
                    toCity,
                    seatsCount,
                    totalAmt,
                    safe(b.getStatus()),
                    busNumber,
                    busType,
                    bookingDate);
        }).collect(Collectors.toList());
    }

    public List<PaymentReportResponse> getPaymentReport() {
        return paymentRepository.findAll().stream().map(p -> {
            Long bookingId = (p.getBooking() != null) ? p.getBooking().getId() : null;

            String passenger = "";
            if (p.getBooking() != null && p.getBooking().getUser() != null) {
                passenger = firstNonBlank(p.getBooking().getUser().getName(),
                        p.getBooking().getUser().getEmail());
            }

            double amount = toDouble(p.getAmount());

            String method = "";
            Date paymentDate = null;

            return new PaymentReportResponse(
                    p.getId(),
                    bookingId,
                    passenger,
                    amount,
                    method,
                    safe(p.getStatus()),
                    paymentDate);
        }).collect(Collectors.toList());
    }

    public Map<String, Object> getSalesSummary(LocalDate from, LocalDate to) {
        LocalDate today = LocalDate.now();
        LocalDate start = (from == null) ? today.minusYears(100) : from;
        LocalDate end = (to == null) ? today : to;

        List<Payment> payments = paymentRepository.findAll();

        Set<String> okStatuses = new HashSet<>(Arrays.asList("SUCCESS", "COMPLETED", "PAID"));
        List<Payment> okPayments = payments.stream()
                .filter(p -> {
                    String s = safe(p.getStatus()).toUpperCase(Locale.ROOT);
                    return s.isEmpty() || okStatuses.contains(s);
                })
                .collect(Collectors.toList());

        double totalRevenue = okPayments.stream()
                .mapToDouble(p -> toDouble(p.getAmount()))
                .sum();

        Map<LocalDate, Double> byDay = new TreeMap<>();
        for (Payment p : okPayments) {
            Booking b = p.getBooking();
            Instant bInst = (b != null) ? b.getBookingDate() : null;
            if (bInst == null)
                continue;

            LocalDate day = bInst.atZone(ZoneId.systemDefault()).toLocalDate();
            if (day.isBefore(start) || day.isAfter(end))
                continue;

            double a = toDouble(p.getAmount());
            byDay.merge(day, a, Double::sum);
        }

        List<Map<String, Object>> revenueByDay = byDay.entrySet().stream()
                .map(e -> {
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("date", e.getKey().toString());
                    m.put("revenue", round2(e.getValue()));
                    return m;
                })
                .collect(Collectors.toList());

        Map<String, Object> out = new LinkedHashMap<>();
        out.put("from", start.toString());
        out.put("to", end.toString());
        out.put("totalBookings", bookingRepository.count());
        out.put("totalPayments", payments.size());
        out.put("totalRevenue", round2(totalRevenue));
        out.put("revenueByDay", revenueByDay);
        return out;
    }

    public ResponseEntity<byte[]> downloadBookingReportPdf() throws Exception {
        List<BookingReportResponse> reports = getBookingReport();

        Document doc = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter.getInstance(doc, out);

        doc.open();
        doc.add(new Paragraph("Booking Report"));
        doc.add(new Paragraph("======================================"));
        for (BookingReportResponse r : reports) {
            doc.add(new Paragraph(
                    "Ticket: " + safe(r.getTicketNumber()) +
                            " | Passenger: " + safe(r.getPassenger()) +
                            " | Trip: " + safe(r.getFromCity()) + " -> " + safe(r.getToCity()) +
                            " | Seats: " + r.getSeats() +
                            " | Amount: " + r.getTotalAmount() +
                            " | Status: " + safe(r.getStatus())));
        }
        doc.close();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=booking-report.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(out.toByteArray());
    }

    public ResponseEntity<byte[]> downloadPaymentReportPdf() throws Exception {
        List<PaymentReportResponse> reports = getPaymentReport();

        Document doc = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter.getInstance(doc, out);

        doc.open();
        doc.add(new Paragraph("Payment Report"));
        doc.add(new Paragraph("======================================"));
        for (PaymentReportResponse r : reports) {
            doc.add(new Paragraph(
                    "PaymentId: " + r.getPaymentId() +
                            " | BookingId: " + r.getBookingId() +
                            " | Passenger: " + safe(r.getPassenger()) +
                            " | Amount: " + r.getAmount() +
                            " | Method: " + safe(r.getPaymentMethod()) +
                            " | Status: " + safe(r.getStatus())));
        }
        doc.close();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=payment-report.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(out.toByteArray());
    }

    // -------- helpers --------
    private static String safe(String s) {
        return (s == null) ? "" : s;
    }

    private static String firstNonBlank(String... vals) {
        for (String v : vals)
            if (v != null && !v.isBlank())
                return v;
        return "";
    }

    private static double toDouble(Object amount) {
        if (amount == null)
            return 0.0;
        if (amount instanceof BigDecimal)
            return ((BigDecimal) amount).doubleValue();
        if (amount instanceof Number)
            return ((Number) amount).doubleValue();
        try {
            return Double.parseDouble(amount.toString());
        } catch (Exception e) {
            return 0.0;
        }
    }

    private static double round2(double x) {
        return Math.round(x * 100.0) / 100.0;
    }
}
