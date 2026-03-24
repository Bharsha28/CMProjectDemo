package com.CardMaster.model.paa;

import com.CardMaster.model.cau.UnderwritingDecision;
import com.CardMaster.model.cpl.CardProduct;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

import com.CardMaster.model.cau.CreditScore;

@Data
@Entity
@Table(name = "card_application")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardApplication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long applicationId;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private CardProduct product;

    private Double requestedLimit;

    private LocalDate applicationDate;

    @Enumerated(EnumType.STRING)
    private CardApplicationStatus status;

    @OneToMany(mappedBy = "application", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Document> documents;

    @OneToMany(mappedBy = "application", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CreditScore> creditScores;

    @OneToMany(mappedBy = "application", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UnderwritingDecision> decisions;

    public enum CardApplicationStatus {
        Submitted, UnderReview, Approved, Rejected
    }
}
