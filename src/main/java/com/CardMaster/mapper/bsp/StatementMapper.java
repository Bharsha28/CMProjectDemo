package com.CardMaster.mapper.bsp;

import com.CardMaster.dto.bsp.StatementDto;
import com.CardMaster.model.bsp.Statement;
import com.CardMaster.Enum.bsp.StatementStatus;
import com.CardMaster.model.cias.CardAccount;
import com.CardMaster.dao.cias.CardAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StatementMapper {

    private final CardAccountRepository accountRepository;

    // DTO → Entity
    public Statement toEntity(StatementDto dto) {
        CardAccount account = accountRepository.findById(dto.getAccountId())
                .orElseThrow(() -> new IllegalArgumentException("Account not found with ID: " + dto.getAccountId()));

        Statement st = new Statement();
        st.setAccount(account);
        st.setPeriodStart(dto.getPeriodStart());
        st.setPeriodEnd(dto.getPeriodEnd());
        st.setTotalDue(dto.getTotalDue());
        st.setMinimumDue(dto.getMinimumDue());
        st.setGeneratedDate(dto.getGeneratedDate());
        st.setStatus(dto.getStatus()); // already an enum, no need for valueOf
        return st;
    }

    // Entity → DTO
    public StatementDto toDTO(Statement st) {
        StatementDto dto = new StatementDto();
        dto.setStatementId(st.getStatementId());
        dto.setAccountId(st.getAccount().getAccountId());
        dto.setPeriodStart(st.getPeriodStart());
        dto.setPeriodEnd(st.getPeriodEnd());
        dto.setTotalDue(st.getTotalDue());
        dto.setMinimumDue(st.getMinimumDue());
        dto.setGeneratedDate(st.getGeneratedDate());
        dto.setStatus(st.getStatus());
        return dto;
    }
}
