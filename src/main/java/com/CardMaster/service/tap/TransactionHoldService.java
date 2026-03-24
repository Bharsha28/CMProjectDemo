package com.CardMaster.service.tap;

import com.CardMaster.dao.tap.TransactionHoldRepository;
import com.CardMaster.dao.tap.TransactionRepository;
import com.CardMaster.model.tap.Transaction;
import com.CardMaster.model.tap.TransactionHold;
import com.CardMaster.exceptions.tap.TransactionNotFoundException;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionHoldService {

    private final TransactionHoldRepository holdRepo;
    private final TransactionRepository transactionRepo;

    /**
     * Create a hold manually.
     */
    @Transactional
    public TransactionHold create(TransactionHold hold) {

        Long txId = hold.getTransaction().getTransactionId();

        // Ensure transaction exists
        Transaction tx = transactionRepo.findById(txId)
                .orElseThrow(() -> new TransactionNotFoundException(txId));

        hold.setTransaction(tx);

        // Default holdDate if not set
        if (hold.getHoldDate() == null) {
            hold.setHoldDate(LocalDateTime.now());
        }

        return holdRepo.save(hold);
    }

    /**
     * Get a hold by ID.
     */
    @Transactional(readOnly = true)
    public TransactionHold getById(Long id) {
        return holdRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Hold not found with id: " + id));
    }

    /**
     * List all holds for a given transaction.
     */
    @Transactional(readOnly = true)
    public List<TransactionHold> listByTransaction(Long transactionId) {
        return holdRepo.findByTransaction_TransactionId(transactionId);
    }

    /**
     * Release a hold (set releaseDate).
     */
    @Transactional
    public TransactionHold release(Long id) {

        TransactionHold hold = holdRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Hold not found with id: " + id));

        if (hold.getReleaseDate() == null) {
            hold.setReleaseDate(LocalDateTime.now());
            hold = holdRepo.save(hold);
        }

        return hold;
    }
}
