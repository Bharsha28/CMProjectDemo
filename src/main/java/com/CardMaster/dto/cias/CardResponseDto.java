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
public class CardResponseDto {
        private Long cardId;
        private Long applicationId;
        private Long customerId;
        private Long productId;
        private String maskedCardNumber;
        private LocalDate expiryDate;
        private String status; // ISSUED, ACTIVE, BLOCKED


}
