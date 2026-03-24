package com.CardMaster.dto.cau;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreditScoreResponse {
    private Long scoreId;
    private Long applicationId;
    private Integer bureauScore;
    private Integer internalScore;
    private String generatedDate;
}