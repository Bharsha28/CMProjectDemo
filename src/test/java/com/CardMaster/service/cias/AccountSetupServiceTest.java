package com.CardMaster.service.cias;

import com.CardMaster.Enum.cias.AccountStatus;
import com.CardMaster.Enum.cias.CardStatus;
import com.CardMaster.Enum.cau.UnderwritingDecisionType;
import com.CardMaster.dao.cau.UnderwritingDecisionRepository;
import com.CardMaster.dao.cias.CardAccountRepository;
import com.CardMaster.dao.cias.CardRepository;
import com.CardMaster.dto.cias.CardAccountRequestDto;
import com.CardMaster.model.cau.UnderwritingDecision;
import com.CardMaster.model.cias.Card;
import com.CardMaster.model.cias.CardAccount;
import com.CardMaster.model.paa.CardApplication;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AccountSetupServiceTest {

    @Mock
    private CardAccountRepository accountRepository;

    @Mock
    private CardRepository cardRepository;

    @Mock
    private UnderwritingDecisionRepository decisionRepository;

    @InjectMocks
    private AccountSetupService accountSetupService;

    private CardAccountRequestDto requestDto;
    private Card card;
    private CardAccount account;
    private UnderwritingDecision decision;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        requestDto = new CardAccountRequestDto();
        requestDto.setCardId(1L);

        card = new Card();
        card.setCardId(1L);
        card.setMaskedCardNumber("**** **** **** 4321");
        card.setExpiryDate(LocalDate.of(2031, 3, 1));
        card.setStatus(CardStatus.ISSUED);
        CardApplication application = new CardApplication();
        application.setApplicationId(99L);
        card.setApplication(application);

        account = new CardAccount();
        account.setAccountId(10L);
        account.setCard(card);
        account.setCreditLimit(50000.0);
        account.setAvailableLimit(50000.0);
        account.setOpenDate(LocalDate.now());
        account.setStatus(AccountStatus.ACTIVE);

        decision = new UnderwritingDecision();
        decision.setDecision(UnderwritingDecisionType.APPROVE);
        decision.setApprovedLimit(50000.0);
    }

    @Test
    void testCreateAccountActivatesCard() {
        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));
        when(accountRepository.findByCardCardId(1L)).thenReturn(Optional.empty());
        when(decisionRepository.findTopByApplication_ApplicationIdOrderByDecisionDateDesc(99L)).thenReturn(Optional.of(decision));
        when(accountRepository.save(any(CardAccount.class))).thenReturn(account);

        CardAccount result = accountSetupService.createAccount(requestDto);

        assertNotNull(result);
        assertEquals(AccountStatus.ACTIVE, result.getStatus());
        assertEquals(CardStatus.ACTIVE, result.getCard().getStatus());
        verify(cardRepository, times(1)).save(card);
        verify(accountRepository, times(1)).save(any(CardAccount.class));
    }

    @Test
    void testCreateAccountRequiresNoExistingAccount() {
        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));
        when(accountRepository.findByCardCardId(1L)).thenReturn(Optional.of(account));

        assertThrows(IllegalStateException.class, () -> accountSetupService.createAccount(requestDto));
    }

    @Test
    void testGetAccountById() {
        when(accountRepository.findById(10L)).thenReturn(Optional.of(account));

        CardAccount result = accountSetupService.getAccountById(10L);

        assertNotNull(result);
        assertEquals(10L, result.getAccountId());
        verify(accountRepository, times(1)).findById(10L);
    }

    @Test
    void testGetAccountByIdNotFound() {
        when(accountRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> accountSetupService.getAccountById(99L));
    }

    @Test
    void testUseCardReducesAvailableLimit() {
        when(accountRepository.findById(10L)).thenReturn(Optional.of(account));
        when(accountRepository.save(account)).thenReturn(account);

        CardAccount result = accountSetupService.useCard(10L, 5000.0);

        assertEquals(45000.0, result.getAvailableLimit());
        verify(accountRepository, times(1)).save(account);
    }

    @Test
    void testUseCardInsufficientLimit() {
        account.setAvailableLimit(1000.0);
        when(accountRepository.findById(10L)).thenReturn(Optional.of(account));

        assertThrows(IllegalStateException.class, () -> accountSetupService.useCard(10L, 5000.0));
    }
}
