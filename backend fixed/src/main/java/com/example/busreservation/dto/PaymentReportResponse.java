package com.example.busreservation.dto;

import java.util.Date;

public class PaymentReportResponse {
    private Long paymentId;
    private Long bookingId;
    private String passenger;
    private double amount;
    private String paymentMethod;
    private String status;
    private Date paymentDate;

    public PaymentReportResponse() {
    }

    public PaymentReportResponse(Long paymentId, Long bookingId, String passenger, double amount,
            String paymentMethod, String status, Date paymentDate) {
        this.paymentId = paymentId;
        this.bookingId = bookingId;
        this.passenger = passenger;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.status = status;
        this.paymentDate = paymentDate;
    }

    // getters & setters
    public Long getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Long paymentId) {
        this.paymentId = paymentId;
    }

    public Long getBookingId() {
        return bookingId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }

    public String getPassenger() {
        return passenger;
    }

    public void setPassenger(String passenger) {
        this.passenger = passenger;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(Date paymentDate) {
        this.paymentDate = paymentDate;
    }
}
