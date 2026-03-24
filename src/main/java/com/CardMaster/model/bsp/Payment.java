package com.CardMaster.model.bsp;

import com.CardMaster.Enum.bsp.PaymentMethod;
import com.CardMaster.Enum.bsp.PaymentStatus;
import com.CardMaster.model.bsp.Statement;
import com.CardMaster.model.cias.CardAccount;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Data
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private CardAccount account;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "statement_id")
    private Statement statement;

    @NotNull @Positive
    private Double amount;

    @NotNull
    private LocalDateTime paymentDate;

    @Enumerated(EnumType.STRING)
    @NotNull
    private PaymentMethod method;

    @Enumerated(EnumType.STRING)
    @NotNull
    private PaymentStatus status;
}
