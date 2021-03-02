package org.manu.spring.statemachine.domain;

public enum PaymentEvent {
    PRE_AUTHORIZE, PRE_AUTH_APPROVED, PRE_AUTH_DECLINED, AUTH_APPROVED, AUTH_DECLINED
}
