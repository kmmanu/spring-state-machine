package org.manu.spring.statemachine.domain;


import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Payment {
    @Id
    @GeneratedValue
    private Long id;

    @Enumerated(EnumType.STRING)
    private PaymentState paymentState;

    private BigDecimal amount;
}
