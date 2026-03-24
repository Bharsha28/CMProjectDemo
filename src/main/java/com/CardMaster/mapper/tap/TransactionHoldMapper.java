package com.CardMaster.mapper.tap;

import com.CardMaster.dto.tap.TransactionHoldDto;
import com.CardMaster.model.tap.TransactionHold;
import com.CardMaster.model.tap.Transaction;
import com.CardMaster.dao.tap.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class TransactionHoldMapper {

    private final TransactionRepository transactionRepository;

    // DTO → Entity
    public TransactionHold toEntity(TransactionHoldDto dto) {
        Transaction transaction = transactionRepository.findById(dto.getTransactionId())
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found with ID: " + dto.getTransactionId()));

        TransactionHold hold = new TransactionHold();
        hold.setTransaction(transaction);
        hold.setAmount(dto.getAmount());
        hold.setHoldDate(dto.getHoldDate());
        hold.setReleaseDate(dto.getReleaseDate());
        return hold;
    }

    // Entity → DTO
    public TransactionHoldDto toDTO(TransactionHold hold) {
        TransactionHoldDto dto = new TransactionHoldDto();
        dto.setHoldId(hold.getHoldId());
        dto.setTransactionId(hold.getTransaction().getTransactionId());
        dto.setAmount(hold.getAmount());
        dto.setHoldDate(hold.getHoldDate());
        dto.setReleaseDate(hold.getReleaseDate());
        return dto;
    }
}
