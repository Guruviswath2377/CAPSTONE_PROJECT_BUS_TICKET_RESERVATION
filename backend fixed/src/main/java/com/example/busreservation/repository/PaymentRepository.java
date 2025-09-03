package com.example.busreservation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.busreservation.model.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
