package com.CardMaster.dto.tap;

import com.CardMaster.Enum.tap.TransactionChannel;
import com.CardMaster.Enum.tap.TransactionStatus;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDto {
    private Long transactionId;
    private Long accountId;
    private Double amount;
    private String currency;
    private String merchant;
    private TransactionChannel channel;
    private LocalDateTime transactionDate;
    private TransactionStatus status;
}
