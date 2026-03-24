
package com.CardMaster.dto.cau;
import com.CardMaster.Enum.cau.UnderwritingDecisionType;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UnderwritingDecisionRequest {

    private UnderwritingDecisionType decision;
    @PositiveOrZero(message = "Approved limit cannot be negative")
    private Double approvedLimit;
    @Size(max=500)
    private String remarks;
}



