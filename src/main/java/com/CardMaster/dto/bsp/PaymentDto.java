package com.CardMaster.dto.bsp;

import com.CardMaster.Enum.bsp.PaymentMethod;
import com.CardMaster.Enum.bsp.PaymentStatus;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDto {
    private Long paymentId;
    private Long accountId;
    private Long statementId;
    private Double amount;
    private LocalDateTime paymentDate;
    private PaymentMethod method;
    private PaymentStatus status;
}
