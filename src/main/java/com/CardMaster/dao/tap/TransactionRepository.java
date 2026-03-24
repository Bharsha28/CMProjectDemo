package com.CardMaster.dao.tap;

import com.CardMaster.model.tap.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByAccount_AccountId(Long accountId);

    List<Transaction> findByAccount_Card_Customer_ContactInfo_Email(String email);

    List<Transaction> findByAccount_AccountIdAndTransactionDateBetween(Long accountId, LocalDateTime start, LocalDateTime end);
}
