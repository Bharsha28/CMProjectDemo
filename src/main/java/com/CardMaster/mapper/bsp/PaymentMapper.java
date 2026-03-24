package com.CardMaster.mapper.bsp;

import com.CardMaster.dto.bsp.PaymentDto;
import com.CardMaster.model.bsp.Statement;
import com.CardMaster.model.bsp.Payment;
import com.CardMaster.model.cias.CardAccount;
import com.CardMaster.dao.bsp.StatementRepository;
import com.CardMaster.dao.cias.CardAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class PaymentMapper {

    private final CardAccountRepository accountRepository;
    private final StatementRepository statementRepository;

    // DTO → Entity
    public Payment toEntity(PaymentDto dto) {
        CardAccount account = accountRepository.findById(dto.getAccountId())
                .orElseThrow(() -> new IllegalArgumentException("Account not found with ID: " + dto.getAccountId()));

        Payment p = new Payment();
        p.setAccount(account);
        if (dto.getStatementId() != null) {
            Statement statement = statementRepository.findById(dto.getStatementId())
                    .orElseThrow(() -> new IllegalArgumentException("Statement not found with ID: " + dto.getStatementId()));
            p.setStatement(statement);
        }
        p.setAmount(dto.getAmount());
        p.setPaymentDate(dto.getPaymentDate());
        p.setMethod(dto.getMethod());
        p.setStatus(dto.getStatus());
        return p;
    }

    // Entity → DTO
    public PaymentDto toDTO(Payment p) {
        PaymentDto dto = new PaymentDto();
        dto.setPaymentId(p.getPaymentId());
        dto.setAccountId(p.getAccount().getAccountId());
        dto.setStatementId(p.getStatement() != null ? p.getStatement().getStatementId() : null);
        dto.setAmount(p.getAmount());
        dto.setPaymentDate(p.getPaymentDate());
        dto.setMethod(p.getMethod());
        dto.setStatus(p.getStatus());
        return dto;
    }
}
