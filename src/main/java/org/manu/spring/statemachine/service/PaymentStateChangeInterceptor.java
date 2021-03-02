package org.manu.spring.statemachine.service;

import lombok.RequiredArgsConstructor;
import org.manu.spring.statemachine.domain.Payment;
import org.manu.spring.statemachine.domain.PaymentEvent;
import org.manu.spring.statemachine.domain.PaymentState;
import org.manu.spring.statemachine.repository.PaymentRepository;
import org.springframework.messaging.Message;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.support.StateMachineInterceptorAdapter;
import org.springframework.statemachine.transition.Transition;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static org.manu.spring.statemachine.service.PaymentServiceImpl.PAYMENT_ID_HEADER;

@Component
@RequiredArgsConstructor
public class PaymentStateChangeInterceptor extends StateMachineInterceptorAdapter<PaymentState, PaymentEvent> {
    private final PaymentRepository paymentRepository;

    @Override
    public void preStateChange(State<PaymentState, PaymentEvent> state,
                               Message<PaymentEvent> message,
                               Transition<PaymentState, PaymentEvent> transition,
                               StateMachine<PaymentState, PaymentEvent> stateMachine,
                               StateMachine<PaymentState, PaymentEvent> rootStateMachine) {
        Optional.ofNullable(message)
                .flatMap(msg -> Optional.ofNullable((Long) msg.getHeaders().getOrDefault(PAYMENT_ID_HEADER, -1L)))
                .ifPresent(paymentId -> {
                    Payment payment = paymentRepository.getOne(paymentId);
                    payment.setState(state.getId());
                    paymentRepository.save(payment);
                });
    }
}
