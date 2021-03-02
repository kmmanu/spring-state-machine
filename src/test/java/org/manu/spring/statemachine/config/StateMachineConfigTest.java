package org.manu.spring.statemachine.config;

import org.junit.jupiter.api.Test;
import org.manu.spring.statemachine.domain.PaymentEvent;
import org.manu.spring.statemachine.domain.PaymentState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.manu.spring.statemachine.domain.PaymentEvent.*;
import static org.manu.spring.statemachine.domain.PaymentState.*;

@SpringBootTest
class StateMachineConfigTest {

    @Autowired
    private StateMachineFactory<PaymentState, PaymentEvent> factory;

    @Test
    void testInitialStateChange() {
        // Given
        StateMachine<PaymentState, PaymentEvent> stateMachine = factory.getStateMachine(UUID.randomUUID());
        // When
        stateMachine.start();
        //Then
        assertThat(stateMachine.getState().getId()).isEqualTo(NEW);
    }

    @Test
    void testPreAuthStateChange() {
        StateMachine<PaymentState, PaymentEvent> stateMachine = factory.getStateMachine(UUID.randomUUID());
        stateMachine.start();
        stateMachine.sendEvent(PRE_AUTHORIZE);
        assertThat(stateMachine.getState().getId()).isEqualTo(NEW);
    }

    @Test
    void testPreAuthErrorStateChange() {
        StateMachine<PaymentState, PaymentEvent> stateMachine = factory.getStateMachine(UUID.randomUUID());
        stateMachine.start();
        stateMachine.sendEvent(PRE_AUTH_DECLINED);
        assertThat(stateMachine.getState().getId()).isEqualTo(PRE_AUTH_ERROR);
    }


    @Test
    void testStateChange() {
        StateMachine<PaymentState, PaymentEvent> stateMachine = factory.getStateMachine(UUID.randomUUID());
        stateMachine.start();
        stateMachine.sendEvent(PRE_AUTH_APPROVED);
        assertThat(stateMachine.getState().getId()).isEqualTo(PRE_AUTH);
    }
}