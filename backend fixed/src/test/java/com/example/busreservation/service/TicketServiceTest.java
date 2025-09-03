package com.example.busreservation.service;

import com.example.busreservation.model.*;
import com.example.busreservation.repository.BookingRepository;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.io.ByteArrayInputStream;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TicketServiceTest {

  @Mock
  private BookingRepository bookingRepository;

  @InjectMocks
  private TicketService ticketService;

  @Test
  void generateTicketPdf_containsText_andQrImage() throws Exception {
    Booking booking = sampleBooking(77L, "qr@x.com");

    byte[] pdf = ticketService.generateTicketPdf(booking);
    assertThat(pdf).isNotEmpty();

    try (PDDocument doc = PDDocument.load(new ByteArrayInputStream(pdf))) {
      String text = new PDFTextStripper().getText(doc);
      assertThat(text).contains("Bus Ticket Reservation System");
      assertThat(text).contains("Ticket / Booking ID: 77");
      assertThat(text).contains("Scan the QR");

      int imageCount = 0;
      for (PDPage page : doc.getPages()) {
        PDResources res = page.getResources();
        if (res == null)
          continue;
        for (COSName name : res.getXObjectNames()) {
          PDXObject xo = res.getXObject(name);
          if (xo instanceof PDImageXObject) {
            imageCount++;
          }
        }
      }
      assertThat(imageCount).isGreaterThan(0);
    }
  }

  @Test
  void downloadTicket_returnsAttachmentPdf() throws Exception {
    Booking booking = sampleBooking(12L, "scan@x.com");
    when(bookingRepository.findById(12L)).thenReturn(Optional.of(booking));
    ResponseEntity<byte[]> resp = ticketService.downloadTicket(12L);
    assertThat(resp.getHeaders().getContentDisposition().getFilename()).isEqualTo("ticket-12.pdf");
    assertThat(resp.getBody()).isNotEmpty();
  }

  private Booking sampleBooking(Long id, String email) {
    User u = new User();
    u.setEmail(email);
    u.setName("Test User");

    Route route = new Route();
    route.setSource("CityA");
    route.setDestination("CityB");
    Bus bus = new Bus();
    bus.setBusNumber("KA-01-AB-1234");

    Trip trip = new Trip();
    trip.setRoute(route);
    trip.setBus(bus);
    trip.setDepartureTime(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant());
    trip.setArrivalTime(LocalDateTime.now().plusHours(5).atZone(ZoneId.systemDefault()).toInstant());
    trip.setFare(499.0);

    Seat s1 = new Seat();
    s1.setSeatNumber("1");
    Seat s2 = new Seat();
    s2.setSeatNumber("2");
    Seat s3 = new Seat();
    s3.setSeatNumber("3");

    BookingSeat bs1 = new BookingSeat();
    bs1.setSeat(s1);
    BookingSeat bs2 = new BookingSeat();
    bs2.setSeat(s2);
    BookingSeat bs3 = new BookingSeat();
    bs3.setSeat(s3);

    Booking b = new Booking();
    b.setId(id);
    b.setUser(u);
    b.setTrip(trip);
    b.setSeats(List.of(bs1, bs2, bs3));
    b.setTotalAmount(499.0);
    b.setStatus("CONFIRMED");
    b.setBookingDate(Instant.now());
    return b;
  }
}
