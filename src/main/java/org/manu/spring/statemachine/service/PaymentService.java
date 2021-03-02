package org.manu.spring.statemachine.service;

import org.manu.spring.statemachine.domain.Payment;
import org.manu.spring.statemachine.domain.PaymentEvent;
import org.manu.spring.statemachine.domain.PaymentState;
import org.springframework.statemachine.StateMachine;

public interface PaymentService {

    Payment newPayment(Payment payment);

    StateMachine<PaymentState, PaymentEvent> preAuth(Long paymentId);

    StateMachine<PaymentState, PaymentEvent> authorizePayment(Long paymentId);

    StateMachine<PaymentState, PaymentEvent> declineAuth(Long paymentId);
}
