package com.CardMaster.service.bsp;

import com.CardMaster.dao.bsp.StatementRepository;
import com.CardMaster.dao.cias.CardAccountRepository;
import com.CardMaster.dao.tap.TransactionRepository;
import com.CardMaster.exceptions.bsp.StatementNotFoundException;
import com.CardMaster.model.bsp.Statement;
import com.CardMaster.model.cias.CardAccount;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class StatementServiceTest {

    @Mock
    private StatementRepository statementRepository;

    @Mock
    private CardAccountRepository accountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private StatementService service;

    private Statement statement;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        // Create a CardAccount entity stub
        CardAccount account = new CardAccount();
        account.setAccountId(100L);

        // Create a Statement entity stub
        statement = new Statement();
        statement.setStatementId(1L);
        statement.setAccount(account); // assign entity, not a long
        statement.setPeriodStart(LocalDate.now().minusMonths(1));
        statement.setPeriodEnd(LocalDate.now());
        statement.setTotalDue(5000.0);
        statement.setMinimumDue(500.0);
        statement.setGeneratedDate(LocalDate.now());
    }

    @Test
    void testGenerateStatementThrowsWhenAccountMissing() {
        when(accountRepository.findById(100L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> service.generateStatement(statement));
    }

    @Test
    void testCloseStatementThrowsWhenMissing() {
        when(statementRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(StatementNotFoundException.class,
                () -> service.closeStatement(1L));
    }

    @Test
    void testGetByIdThrowsWhenMissing() {
        when(statementRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(StatementNotFoundException.class,
                () -> service.getById(1L));
    }

    @Test
    void testListAllReturnsRepositoryData() {
        when(statementRepository.findAll()).thenReturn(java.util.List.of(statement));

        assertEquals(1, service.listAll().size());
    }
}
