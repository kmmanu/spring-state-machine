package org.manu.spring.statemachine.config;

import lombok.extern.slf4j.Slf4j;
import org.manu.spring.statemachine.domain.PaymentEvent;
import org.manu.spring.statemachine.domain.PaymentState;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;

import java.util.EnumSet;
import java.util.Random;

import static org.manu.spring.statemachine.domain.PaymentEvent.*;
import static org.manu.spring.statemachine.domain.PaymentState.*;
import static org.manu.spring.statemachine.service.PaymentServiceImpl.PAYMENT_ID_HEADER;

@EnableStateMachineFactory
@Configuration
@Slf4j
public class StateMachineConfig extends EnumStateMachineConfigurerAdapter<PaymentState, PaymentEvent> {
    @Override
    public void configure(StateMachineStateConfigurer<PaymentState, PaymentEvent> states) throws Exception {
        states.withStates()
                .initial(NEW)
                .states(EnumSet.allOf(PaymentState.class))
                .end(AUTH)
                .end(AUTH_ERROR)
                .end((PRE_AUTH_ERROR));
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<PaymentState, PaymentEvent> transitions) throws Exception {
        transitions
                .withExternal().source(NEW).target(NEW).event(PRE_AUTHORIZE)
                .action(preAuthAction())
                .and()
                .withExternal().source(NEW).target(PRE_AUTH).event(PRE_AUTH_APPROVED)
                .and()
                .withExternal().source(NEW).target(PRE_AUTH_ERROR).event(PRE_AUTH_DECLINED);
    }

    @Override
    public void configure(StateMachineConfigurationConfigurer<PaymentState, PaymentEvent> config) throws Exception {
        StateMachineListenerAdapter<PaymentState, PaymentEvent> listener = new StateMachineListenerAdapter<>() {
            @Override
            public void stateChanged(State<PaymentState, PaymentEvent> from, State<PaymentState, PaymentEvent> to) {
                log.info("State changed from {} to {}", from, to);
            }
        };

        config.withConfiguration().listener(listener);
    }

    public Action<PaymentState, PaymentEvent> preAuthAction() {
        return stateContext -> {
            System.out.println("Pre auth was called");
            if (new Random().nextInt(10) < 8) {
                // This can be a ws call which will decide if the action is success or failure
                System.out.println("Approved pre auth");
                stateContext.getStateMachine().sendEvent(MessageBuilder.withPayload(PRE_AUTH_APPROVED)
                        .setHeader(PAYMENT_ID_HEADER, stateContext.getMessageHeader(PAYMENT_ID_HEADER))
                        .build());
            } else {
                System.out.println("Declined!  No credit!!");
                stateContext.getStateMachine().sendEvent(MessageBuilder.withPayload(PRE_AUTH_DECLINED)
                        .setHeader(PAYMENT_ID_HEADER, stateContext.getMessageHeader(PAYMENT_ID_HEADER))
                        .build());
            }
        };
    }
}
