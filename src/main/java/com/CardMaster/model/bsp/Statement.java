package com.CardMaster.model.bsp;

import com.CardMaster.Enum.bsp.StatementStatus;
import com.CardMaster.model.cias.CardAccount;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;


@Entity
@Table(name = "statements")
@Data
public class Statement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long statementId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private CardAccount account;

    @NotNull private LocalDate periodStart;
    @NotNull private LocalDate periodEnd;

    @NotNull @PositiveOrZero
    private Double totalDue;

    @NotNull @PositiveOrZero
    private Double minimumDue;

    @NotNull
    private LocalDate generatedDate;

    @Enumerated(EnumType.STRING)
    @NotNull
    private StatementStatus status;

    @OneToMany(mappedBy = "statement", cascade = CascadeType.ALL)
    private List<Payment> payments;
}
