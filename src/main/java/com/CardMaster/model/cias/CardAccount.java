package com.CardMaster.model.cias;

import com.CardMaster.model.cias.Card;
import com.CardMaster.Enum.cias.AccountStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

import com.CardMaster.model.bsp.Payment;
import com.CardMaster.model.bsp.Statement;
import com.CardMaster.model.tap.Transaction;

@Entity
@Table(name = "card_accounts")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_id")
    private Long accountId;

    @OneToOne(optional = false)
    @JoinColumn(name = "card_id", nullable = false, unique = true)
    private Card card;

    @Positive
    @Column(name = "credit_limit", nullable = false)
    private Double creditLimit;

    @PositiveOrZero
    @Column(name = "available_limit", nullable = false)
    private Double availableLimit;

    @NotNull
    @Column(name = "open_date", nullable = false)
    private LocalDate openDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private AccountStatus status;  // ACTIVE, CLOSED

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL)
    private List<Transaction> transactions;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL)
    private List<Statement> statements;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL)
    private List<Payment> payments;
}
