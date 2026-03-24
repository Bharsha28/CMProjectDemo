package com.CardMaster.model.cpl;

import com.CardMaster.Enum.cpl.FeeType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "fee_config")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeeConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long feeId;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private com.CardMaster.model.cpl.CardProduct product;

    @Enumerated(EnumType.STRING)
    @Column(name = "fee_type", nullable = false, length = 20)
    private FeeType feeType;

    @Positive
    @Column(nullable = false)
    private Double amount;         // You can switch to BigDecimal later if needed
}
