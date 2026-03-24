package com.CardMaster.model.cpl;

import com.CardMaster.Enum.cpl.CardCategory;
import com.CardMaster.Enum.cpl.ProductStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.util.List;

import com.CardMaster.model.cias.Card;
import com.CardMaster.model.paa.CardApplication;

@Entity
@Data
@Table(name = "card_products")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId; // Use Long (wrapper) for nullability with JPA

    @NotBlank
    @Column(nullable = false, length = 80)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CardCategory category;

    @Positive
    @Column(nullable = false)
    private Double interestRate;

    @Positive
    @Column(nullable = false)
    private Double annualFee;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private ProductStatus status;

    @OneToMany(mappedBy = "product")
    private List<CardApplication> applications;

    @OneToMany(mappedBy = "product")
    private List<Card> cards;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FeeConfig> feeConfigs;
}
