package com.CardMaster.service.tap;

import com.CardMaster.dao.tap.TransactionHoldRepository;
import com.CardMaster.dao.tap.TransactionRepository;
import com.CardMaster.exceptions.tap.TransactionNotFoundException;
import com.CardMaster.model.tap.Transaction;
import com.CardMaster.model.tap.TransactionHold;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TransactionHoldServiceTest {

    @Mock
    private TransactionHoldRepository holdRepo;

    @Mock
    private TransactionRepository transactionRepo;

    @InjectMocks
    private TransactionHoldService service;

    private Transaction transaction;
    private TransactionHold hold;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        transaction = new Transaction();
        transaction.setTransactionId(100L);

        hold = new TransactionHold();
        hold.setHoldId(200L);
        hold.setTransaction(transaction);
        hold.setAmount(500.0);
        hold.setHoldDate(LocalDateTime.now());
    }

    @Test
    void testCreateHoldSuccess() {
        when(transactionRepo.findById(100L)).thenReturn(Optional.of(transaction));
        when(holdRepo.save(any(TransactionHold.class))).thenReturn(hold);

        TransactionHold result = service.create(hold);

        assertEquals(200L, result.getHoldId());
        verify(transactionRepo, times(1)).findById(100L);
        verify(holdRepo, times(1)).save(hold);
    }

    @Test
    void testCreateHoldThrowsWhenTransactionNotFound() {
        when(transactionRepo.findById(100L)).thenReturn(Optional.empty());
        assertThrows(TransactionNotFoundException.class, () -> service.create(hold));
    }

    @Test
    void testGetByIdSuccess() {
        when(holdRepo.findById(200L)).thenReturn(Optional.of(hold));
        TransactionHold result = service.getById(200L);
        assertEquals(200L, result.getHoldId());
    }

    @Test
    void testGetByIdThrowsWhenNotFound() {
        when(holdRepo.findById(999L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> service.getById(999L));
    }

    @Test
    void testListByTransaction() {
        when(holdRepo.findByTransaction_TransactionId(100L)).thenReturn(Collections.singletonList(hold));
        List<TransactionHold> result = service.listByTransaction(100L);
        assertEquals(1, result.size());
        assertEquals(200L, result.get(0).getHoldId());
    }

    @Test
    void testReleaseHoldSuccess() {
        when(holdRepo.findById(200L)).thenReturn(Optional.of(hold));
        when(holdRepo.save(any(TransactionHold.class))).thenReturn(hold);

        TransactionHold result = service.release(200L);

        assertNotNull(result.getReleaseDate());
        verify(holdRepo, times(1)).save(hold);
    }

    @Test
    void testReleaseHoldThrowsWhenNotFound() {
        when(holdRepo.findById(999L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> service.release(999L));
    }
}
