package com.CardMaster.dto.paa;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardApplicationDto {

    private Long applicationId;

    @NotNull
    private Long customerId;   // Reference to Customer entity

    @NotNull
    private Long productId;    // Reference to CardProduct entity

    private Double requestedLimit;

    private LocalDate applicationDate;

    @NotNull
    private String status;     // Enum mapped as String (Submitted, UnderReview, Approved, Rejected)
}
