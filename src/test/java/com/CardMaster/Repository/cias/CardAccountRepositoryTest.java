package com.CardMaster.Repository.cias;

import com.CardMaster.dao.cias.CardAccountRepository;
import com.CardMaster.model.cias.CardAccount;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CardAccountRepositoryTest {

    @Mock
    private CardAccountRepository cardAccountRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindByCardCardId() {
        CardAccount account = new CardAccount();
        account.setAccountId(1L);

        when(cardAccountRepository.findByCardCardId(10L))
                .thenReturn(Optional.of(account));

        Optional<CardAccount> result = cardAccountRepository.findByCardCardId(10L);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getAccountId());
        verify(cardAccountRepository).findByCardCardId(10L);
    }

    @Test
    void testFindByStatus() {
        CardAccount account1 = new CardAccount();
        account1.setAccountId(2L);
        CardAccount account2 = new CardAccount();
        account2.setAccountId(3L);

        when(cardAccountRepository.findByStatus("ACTIVE"))
                .thenReturn(Arrays.asList(account1, account2));

        List<CardAccount> accounts = cardAccountRepository.findByStatus("ACTIVE");

        assertEquals(2, accounts.size());
        verify(cardAccountRepository).findByStatus("ACTIVE");
    }

    @Test
    void testFindByAvailableLimitLessThan() {
        CardAccount account = new CardAccount();
        account.setAccountId(4L);

        when(cardAccountRepository.findByAvailableLimitLessThan(5000.0))
                .thenReturn(Arrays.asList(account));

        List<CardAccount> accounts = cardAccountRepository.findByAvailableLimitLessThan(5000.0);

        assertEquals(1, accounts.size());
        assertEquals(4L, accounts.get(0).getAccountId());
        verify(cardAccountRepository).findByAvailableLimitLessThan(5000.0);
    }
}
