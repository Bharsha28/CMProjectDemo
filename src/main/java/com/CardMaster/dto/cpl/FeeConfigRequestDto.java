package com.CardMaster.dto.cpl;

import com.CardMaster.Enum.cpl.FeeType;
import lombok.Data;

@Data
public class FeeConfigRequestDto {
    private Long productId;
    private FeeType feeType;
    private Double amount;
}