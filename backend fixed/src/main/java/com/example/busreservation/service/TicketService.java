package com.example.busreservation.service;

import com.example.busreservation.model.Booking;
import com.example.busreservation.repository.BookingRepository;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BarcodeQRCode;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;

import java.io.ByteArrayOutputStream;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

@Service
public class TicketService {

        private final BookingRepository bookingRepo;

        public TicketService(BookingRepository bookingRepo) {
                this.bookingRepo = bookingRepo;
        }

        @Transactional(readOnly = true)
        public Booking getBookingOr404(Long id) {
                return bookingRepo.findById(id)
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                                "Booking not found"));
        }

        public boolean isAdmin(Authentication auth) {
                if (auth == null)
                        return false;
                return auth.getAuthorities().stream().anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
        }

        public boolean isOwner(Booking booking, Authentication auth) {
                if (auth == null || booking == null || booking.getUser() == null)
                        return false;
                String email = booking.getUser().getEmail();
                return email != null && email.equalsIgnoreCase(auth.getName());
        }

        @Transactional
        public String cancelBooking(Long id) {
                Booking b = getBookingOr404(id);
                if ("CANCELLED".equalsIgnoreCase(safe(b.getStatus()))) {
                        return "Already cancelled";
                }
                b.setStatus("CANCELLED");
                bookingRepo.save(b);
                return "Booking cancelled";
        }

        @Transactional(readOnly = true)
        public ResponseEntity<byte[]> downloadTicket(Long id) throws Exception {
                Booking booking = getBookingOr404(id);
                byte[] pdf = generateTicketPdf(booking);
                return ResponseEntity.ok()
                                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=ticket-" + id + ".pdf")
                                .contentType(MediaType.APPLICATION_PDF)
                                .body(pdf);
        }

        @Transactional(readOnly = true)
        public byte[] generateTicketPdf(Booking booking) throws Exception {
                Document document = new Document();
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                PdfWriter.getInstance(document, out);

                final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
                                .withZone(ZoneId.systemDefault());

                String source = (booking.getTrip() != null && booking.getTrip().getRoute() != null)
                                ? safe(booking.getTrip().getRoute().getSource())
                                : "";
                String dest = (booking.getTrip() != null && booking.getTrip().getRoute() != null)
                                ? safe(booking.getTrip().getRoute().getDestination())
                                : "";
                String busNumber = (booking.getTrip() != null && booking.getTrip().getBus() != null)
                                ? safe(booking.getTrip().getBus().getBusNumber())
                                : "";

                String seats = (booking.getSeats() != null)
                                ? booking.getSeats().stream()
                                                .map(bs -> bs.getSeat() != null ? safe(bs.getSeat().getSeatNumber())
                                                                : "")
                                                .filter(s -> !s.isEmpty())
                                                .sorted()
                                                .collect(Collectors.joining(", "))
                                : "";

                String dep = (booking.getTrip() != null && booking.getTrip().getDepartureTime() != null)
                                ? dtf.format(booking.getTrip().getDepartureTime())
                                : "";
                String arr = (booking.getTrip() != null && booking.getTrip().getArrivalTime() != null)
                                ? dtf.format(booking.getTrip().getArrivalTime())
                                : "";

                String passengerName = (booking.getUser() != null) ? safe(booking.getUser().getName()) : "";
                String passengerEmail = (booking.getUser() != null) ? safe(booking.getUser().getEmail()) : "";
                Instant issuedAt = (booking.getBookingDate() != null) ? booking.getBookingDate() : Instant.now();
                double amount = (booking.getTotalAmount() != null) ? booking.getTotalAmount() : 0.0;

                String qrPayload = String.format(
                                "{\"id\":%d,\"email\":\"%s\",\"seats\":\"%s\",\"amount\":%.2f,\"issuedAt\":\"%s\"}",
                                booking.getId(), passengerEmail, seats, amount, dtf.format(issuedAt));

                BarcodeQRCode qr = new BarcodeQRCode(qrPayload, 150, 150, null);
                Image qrImage = qr.getImage();
                qrImage.scaleAbsolute(120f, 120f);
                qrImage.setAlignment(Element.ALIGN_RIGHT);

                document.open();
                document.add(new Paragraph("Bus Ticket Reservation System"));
                document.add(new Paragraph("--------------------------------"));
                document.add(new Paragraph("Ticket / Booking ID: " + booking.getId()));
                document.add(new Paragraph("Passenger: " + passengerName));
                document.add(new Paragraph("Email: " + passengerEmail));
                document.add(new Paragraph("Route: " + source + " -> " + dest));
                document.add(new Paragraph("Bus: " + busNumber));
                document.add(new Paragraph("Departure: " + dep));
                document.add(new Paragraph("Arrival: " + arr));
                document.add(new Paragraph("Seats: " + seats));
                document.add(new Paragraph(String.format("Total Amount: %.2f", amount)));
                document.add(new Paragraph("Status: " + safe(booking.getStatus())));
                document.add(new Paragraph("Scan the QR to verify ticket details"));
                document.add(qrImage);
                document.close();

                return out.toByteArray();
        }

        private static String safe(String s) {
                return s == null ? "" : s;
        }
}
