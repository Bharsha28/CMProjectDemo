package com.CardMaster.model.cau;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import com.CardMaster.model.paa.CardApplication;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "credit_score")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreditScore {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long scoreId;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id", nullable = false)
    private CardApplication application;
    @Positive
    private Integer bureauScore;
    @Positive
    private Integer internalScore;
    @NotNull
    private LocalDateTime generatedDate;
}
