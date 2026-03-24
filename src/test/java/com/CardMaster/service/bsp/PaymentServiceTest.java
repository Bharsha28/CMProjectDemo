package com.CardMaster.service.bsp;

import com.CardMaster.dao.bsp.PaymentRepository;
import com.CardMaster.dao.bsp.StatementRepository;
import com.CardMaster.dao.cias.CardAccountRepository;
import com.CardMaster.exceptions.bsp.PaymentFailedException;
import com.CardMaster.model.bsp.Payment;
import com.CardMaster.model.cias.CardAccount;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private StatementRepository statementRepository;

    @Mock
    private CardAccountRepository accountRepository;

    @InjectMocks
    private PaymentService service;

    private Payment payment;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        // Create a CardAccount entity stub
        CardAccount account = new CardAccount();
        account.setAccountId(100L);

        // Create a Payment entity stub
        payment = new Payment();
        payment.setPaymentId(1L);
        payment.setAccount(account); // ✅ assign entity, not a long
        payment.setAmount(1000.0);
        payment.setPaymentDate(LocalDateTime.now());
    }

    @Test
    void testCapturePaymentThrowsWhenAccountMissing() {
        when(accountRepository.findById(100L)).thenReturn(Optional.empty());

        assertThrows(PaymentFailedException.class,
                () -> service.capturePayment(payment));
    }

    @Test
    void testGetByIdThrowsWhenPaymentMissing() {
        when(paymentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(PaymentFailedException.class,
                () -> service.getById(1L));
    }

    @Test
    void testListAllReturnsRepositoryData() {
        when(paymentRepository.findAll()).thenReturn(java.util.List.of(payment));

        assertEquals(1, service.listAll().size());
    }
}
