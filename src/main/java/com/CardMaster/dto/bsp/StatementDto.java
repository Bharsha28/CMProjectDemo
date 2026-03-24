package com.CardMaster.dto.bsp;

import com.CardMaster.Enum.bsp.StatementStatus;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;


/**
 * Data Transfer Object for Statement entity.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatementDto {
    private Long statementId;
    private Long accountId;
    private LocalDate periodStart;
    private LocalDate periodEnd;
    private Double totalDue;
    private Double minimumDue;
    private LocalDate generatedDate;
    private StatementStatus status;
}
