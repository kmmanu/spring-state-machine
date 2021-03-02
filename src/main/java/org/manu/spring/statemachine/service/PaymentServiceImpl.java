package org.manu.spring.statemachine.service;

import lombok.AllArgsConstructor;
import org.manu.spring.statemachine.domain.Payment;
import org.manu.spring.statemachine.domain.PaymentEvent;
import org.manu.spring.statemachine.domain.PaymentState;
import org.manu.spring.statemachine.repository.PaymentRepository;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import static org.manu.spring.statemachine.domain.PaymentState.NEW;

@Service
@AllArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    public static final String PAYMENT_ID_HEADER = "payment_id";
    private PaymentRepository paymentRepository;
    private PaymentStateChangeInterceptor paymentStateChangeInterceptor;
    private StateMachineFactory<PaymentState, PaymentEvent> paymentSmFactory;

    @Override
    @Transactional
    public Payment newPayment(Payment payment) {
        payment.setState(NEW);
        return paymentRepository.save(payment);
    }

    @Override
    @Transactional
    public StateMachine<PaymentState, PaymentEvent> preAuth(Long paymentId) {
        StateMachine<PaymentState, PaymentEvent> sm = build((paymentId));
        sendEvent(paymentId, sm, PaymentEvent.PRE_AUTH_APPROVED);
        return sm;
    }

    @Override
    @Transactional
    public StateMachine<PaymentState, PaymentEvent> authorizePayment(Long paymentId) {
        StateMachine<PaymentState, PaymentEvent> sm = build((paymentId));
        sendEvent(paymentId, sm, PaymentEvent.AUTH_APPROVED);
        return sm;
    }

    @Override
    @Transactional
    public StateMachine<PaymentState, PaymentEvent> declineAuth(Long paymentId) {
        StateMachine<PaymentState, PaymentEvent> sm = build((paymentId));
        sendEvent(paymentId, sm, PaymentEvent.AUTH_DECLINED);
        return sm;
    }

    private void sendEvent(Long paymentId, StateMachine<PaymentState, PaymentEvent> sm, PaymentEvent event){
        Message<PaymentEvent> paymentMsg = MessageBuilder.withPayload(event)
                .setHeader(PAYMENT_ID_HEADER, paymentId)
                .build();
        sm.sendEvent(paymentMsg);
    }

    private StateMachine<PaymentState, PaymentEvent> build(Long paymentId){
        Payment payment = paymentRepository.getOne(paymentId);
        StateMachine<PaymentState, PaymentEvent> sm = paymentSmFactory.getStateMachine(Long.toString(payment.getId()));
        sm.stop();
        sm.getStateMachineAccessor()
                .doWithAllRegions(sma -> {
                    sma.addStateMachineInterceptor(paymentStateChangeInterceptor);
                    sma.resetStateMachine(new DefaultStateMachineContext<>(payment.getState(), null, null, null));
                });
        sm.start();
        return sm;
    }
}
