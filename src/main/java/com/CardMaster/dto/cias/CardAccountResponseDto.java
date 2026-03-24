package com.CardMaster.dto.cias;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardAccountResponseDto {
    private Long accountId;
    private Long cardId;
    private Long applicationId;
    private Double creditLimit;
    private Double availableLimit;
    private LocalDate openDate;
    private String status; // ACTIVE, CLOSED

}
