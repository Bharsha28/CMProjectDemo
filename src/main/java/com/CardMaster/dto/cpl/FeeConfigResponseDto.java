package com.CardMaster.dto.cpl;

import com.CardMaster.Enum.cpl.FeeType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FeeConfigResponseDto {
    private Long feeId;
    private Long productId;
    private FeeType feeType;
    private Double amount;
}