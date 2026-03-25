package com.CardMaster.model.tap;

import com.CardMaster.Enum.tap.TransactionChannel;
import com.CardMaster.Enum.tap.TransactionStatus;
import com.CardMaster.model.cias.CardAccount;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;


@Entity
@Table(name = "transactions")
@Data
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "trx_seq")
    @SequenceGenerator(name = "trx_seq", sequenceName = "trx_seq", initialValue = 600000, allocationSize = 1)
    private Long transactionId;

    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    private CardAccount account;


    @NotNull @Positive
    private Double amount;

    @NotBlank
    private String currency;

    @NotBlank
    private String merchant;

    @Enumerated(EnumType.STRING)
    @NotNull
    private TransactionChannel channel;

    @NotNull
    private LocalDateTime transactionDate;

    @Enumerated(EnumType.STRING)
    @NotNull
    private TransactionStatus status;

    @OneToMany(mappedBy = "transaction", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TransactionHold> holds;
}
