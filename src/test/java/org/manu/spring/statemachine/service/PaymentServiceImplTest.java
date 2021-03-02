package org.manu.spring.statemachine.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.manu.spring.statemachine.domain.Payment;
import org.manu.spring.statemachine.domain.PaymentEvent;
import org.manu.spring.statemachine.domain.PaymentState;
import org.manu.spring.statemachine.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.statemachine.StateMachine;

import javax.transaction.Transactional;
import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class PaymentServiceImplTest {

    @Autowired
    private PaymentService paymentService;
    @Autowired
    private PaymentRepository paymentRepository;
    private Payment payment;

    @BeforeEach
    void setUp() {
        payment = Payment.builder().amount(new BigDecimal("100.00")).build();
    }

    @Test
    @Transactional
    void preAuth() {
        // Given
        Payment paymentInDb = paymentService.newPayment(payment);

        //When
        StateMachine<PaymentState, PaymentEvent> sm = paymentService.preAuth(paymentInDb.getId());

        //Then
        Payment preAuthedPayment = paymentRepository.getOne(paymentInDb.getId());
        assertThat(preAuthedPayment.getState()).isEqualTo(PaymentState.PRE_AUTH);
    }
}