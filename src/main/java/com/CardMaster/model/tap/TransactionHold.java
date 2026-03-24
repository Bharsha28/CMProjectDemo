package com.CardMaster.model.tap;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDateTime;
import com.CardMaster.model.tap.Transaction;


@Entity
@Table(name = "transaction_holds")
@Data
public class TransactionHold {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long holdId;

    @ManyToOne
    @JoinColumn(name = "transaction_id", nullable = false)
    private Transaction transaction;

    @NotNull @Positive
    private Double amount;

    @NotNull
    private LocalDateTime holdDate;

    private LocalDateTime releaseDate;
}
