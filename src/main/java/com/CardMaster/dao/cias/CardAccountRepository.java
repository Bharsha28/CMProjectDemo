package com.CardMaster.dao.cias;

import com.CardMaster.model.cias.CardAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface CardAccountRepository extends JpaRepository<CardAccount, Long> {

    // Find account by card ID (one-to-one relationship)
    Optional<CardAccount> findByCardCardId(Long cardId);

    // Find account by customer email
    Optional<CardAccount> findByCardCustomerContactInfoEmail(String email);

    // Find all accounts by status (ACTIVE / CLOSED)
    List<CardAccount> findByStatus(String status);

    // Find accounts with available limit less than a threshold
    List<CardAccount> findByAvailableLimitLessThan(Double amount);
}
