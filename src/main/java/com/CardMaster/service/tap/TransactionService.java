package com.CardMaster.service.tap;

import com.CardMaster.dao.tap.TransactionRepository;
import com.CardMaster.dao.tap.TransactionHoldRepository;
import com.CardMaster.dao.cias.CardAccountRepository;
import com.CardMaster.Enum.tap.TransactionStatus;
import com.CardMaster.dto.tap.TransactionDto;
import com.CardMaster.exceptions.tap.TransactionNotFoundException;
import com.CardMaster.model.cias.CardAccount;
import com.CardMaster.model.tap.Transaction;
import com.CardMaster.model.tap.TransactionHold;
import com.CardMaster.Enum.cias.AccountStatus;
import com.CardMaster.Enum.cias.CardStatus;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepo;
    private final TransactionHoldRepository holdRepo;
    private final CardAccountRepository accountRepo;

    /**
     * AUTHORIZE a transaction (create a hold + set status).
     */
    @Transactional
    public Transaction authorize(Transaction tx) {
        CardAccount account = accountRepo.findById(tx.getAccount().getAccountId())
                .orElseThrow(() -> new IllegalArgumentException("Account not found with id: " + tx.getAccount().getAccountId()));

        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new IllegalStateException("Only ACTIVE accounts can authorize transactions");
        }
        if (account.getCard().getStatus() != CardStatus.ACTIVE) {
            throw new IllegalStateException("Only ACTIVE cards can authorize transactions");
        }
        if (account.getAvailableLimit() < tx.getAmount()) {
            throw new IllegalStateException("Insufficient available limit");
        }

        tx.setStatus(TransactionStatus.AUTHORIZED);
        tx.setTransactionDate(LocalDateTime.now());
        tx.setAccount(account);

        Transaction saved = transactionRepo.save(tx);

        TransactionHold hold = new TransactionHold();
        hold.setTransaction(saved);
        hold.setAmount(saved.getAmount());
        hold.setHoldDate(LocalDateTime.now());
        holdRepo.save(hold);

        account.setAvailableLimit(account.getAvailableLimit() - saved.getAmount());
        accountRepo.save(account);

        return saved;
    }

    /**
     * POST (capture) an AUTHORIZED transaction.
     */
    @Transactional
    public TransactionDto post(Long id) {

        Transaction tx = transactionRepo.findById(id)
                .orElseThrow(() -> new TransactionNotFoundException(id));

        if (tx.getStatus() != TransactionStatus.AUTHORIZED) {
            throw new IllegalStateException("Only AUTHORIZED transactions can be posted");
        }

        // Find the first hold for this transaction
        TransactionHold hold = holdRepo
                .findFirstByTransaction_TransactionIdAndReleaseDateIsNullOrderByHoldDateAsc(id)
                .orElse(null);

        // Release hold
        if (hold != null) {
            hold.setReleaseDate(LocalDateTime.now());
            holdRepo.save(hold);
        }

        tx.setStatus(TransactionStatus.POSTED);
        tx = transactionRepo.save(tx);

        // Convert Entity → DTO manually (beginner-friendly)
       TransactionDto dto =
                new com.CardMaster.dto.tap.TransactionDto(
                        tx.getTransactionId(),
                        tx.getAccount().getAccountId(),
                        tx.getAmount(),
                        tx.getCurrency(),
                        tx.getMerchant(),
                        tx.getChannel(),
                        tx.getTransactionDate(),
                        tx.getStatus()
                );
        return dto;
    }

    /**
     * REVERSE a transaction.
     */
    @Transactional
    public Transaction reverse(Long id) {

        Transaction tx = transactionRepo.findById(id)
                .orElseThrow(() -> new TransactionNotFoundException(id));

        if (tx.getStatus() != TransactionStatus.AUTHORIZED && tx.getStatus() != TransactionStatus.POSTED) {
            throw new IllegalStateException("Only AUTHORIZED or POSTED transactions can be reversed");
        }

        // Release hold if exists
        TransactionHold hold = holdRepo
                .findFirstByTransaction_TransactionIdAndReleaseDateIsNullOrderByHoldDateAsc(id)
                .orElse(null);

        if (hold != null) {
            hold.setReleaseDate(LocalDateTime.now());
            holdRepo.save(hold);
        }

        tx.setStatus(TransactionStatus.REVERSED);
        CardAccount account = tx.getAccount();
        account.setAvailableLimit(Math.min(account.getCreditLimit(), account.getAvailableLimit() + tx.getAmount()));
        accountRepo.save(account);

        return transactionRepo.save(tx);
    }

    /**
     * Get transaction by ID.
     */
    @Transactional(readOnly = true)
    public Transaction getById(Long id) {
        return transactionRepo.findById(id)
                .orElseThrow(() -> new TransactionNotFoundException(id));
    }

    /**
     * List all transactions.
     */
    @Transactional(readOnly = true)
    public List<Transaction> listAll() {
        return transactionRepo.findAll();
    }

    @Transactional(readOnly = true)
    public List<Transaction> listByEmail(String email) {
        return transactionRepo.findByAccount_Card_Customer_ContactInfo_Email(email);
    }
}


