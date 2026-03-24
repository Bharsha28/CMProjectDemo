package com.CardMaster.dto.cpl;

import com.CardMaster.Enum.cpl.CardCategory;
import com.CardMaster.Enum.cpl.ProductStatus;
import lombok.Data;

@Data
public class CardProductRequestDto {
    private String name;
    private CardCategory category;
    private Double interestRate;
    private Double annualFee;
    private ProductStatus status;
}