package org.manu.spring.statemachine.repository;

import org.manu.spring.statemachine.domain.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
