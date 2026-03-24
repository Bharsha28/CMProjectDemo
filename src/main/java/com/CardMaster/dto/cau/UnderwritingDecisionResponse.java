
package com.CardMaster.dto.cau;

import com.CardMaster.Enum.cau.UnderwritingDecisionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UnderwritingDecisionResponse {
    private Long decisionId;
    private Long applicationId;
    private Long underwriterId;
    private UnderwritingDecisionType decision;
    private Double approvedLimit;
    private String remarks;
    private String decisionDate;
}