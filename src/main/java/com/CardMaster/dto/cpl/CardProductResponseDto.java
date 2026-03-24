package com.CardMaster.dto.cpl;

import com.CardMaster.Enum.cpl.CardCategory;
import com.CardMaster.Enum.cpl.ProductStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CardProductResponseDto {
    private Long productId;
    private String name;
    private CardCategory category;
    private Double interestRate;
    private Double annualFee;
    private ProductStatus status;
}