package com.example.busreservation.dto;

import java.util.Date;

public class BookingReportResponse {
    private Long bookingId;
    private String ticketNumber;
    private String passenger;
    private String fromCity;
    private String toCity;
    private int seats;
    private double totalAmount;
    private String status;
    private String busNumber;
    private String busType;
    private Date bookingDate;

    public BookingReportResponse() {
    }

    public BookingReportResponse(Long bookingId, String ticketNumber, String passenger, String fromCity, String toCity,
            int seats, double totalAmount, String status, String busNumber, String busType,
            Date bookingDate) {
        this.bookingId = bookingId;
        this.ticketNumber = ticketNumber;
        this.passenger = passenger;
        this.fromCity = fromCity;
        this.toCity = toCity;
        this.seats = seats;
        this.totalAmount = totalAmount;
        this.status = status;
        this.busNumber = busNumber;
        this.busType = busType;
        this.bookingDate = bookingDate;
    }

    public Long getBookingId() {
        return bookingId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }

    public String getTicketNumber() {
        return ticketNumber;
    }

    public void setTicketNumber(String ticketNumber) {
        this.ticketNumber = ticketNumber;
    }

    public String getPassenger() {
        return passenger;
    }

    public void setPassenger(String passenger) {
        this.passenger = passenger;
    }

    public String getFromCity() {
        return fromCity;
    }

    public void setFromCity(String fromCity) {
        this.fromCity = fromCity;
    }

    public String getToCity() {
        return toCity;
    }

    public void setToCity(String toCity) {
        this.toCity = toCity;
    }

    public int getSeats() {
        return seats;
    }

    public void setSeats(int seats) {
        this.seats = seats;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getBusNumber() {
        return busNumber;
    }

    public void setBusNumber(String busNumber) {
        this.busNumber = busNumber;
    }

    public String getBusType() {
        return busType;
    }

    public void setBusType(String busType) {
        this.busType = busType;
    }

    public Date getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(Date bookingDate) {
        this.bookingDate = bookingDate;
    }
}
