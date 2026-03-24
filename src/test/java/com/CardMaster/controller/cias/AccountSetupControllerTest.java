package com.CardMaster.controller.cias;

import com.CardMaster.dto.cias.CardAccountRequestDto;
import com.CardMaster.dto.cias.CardAccountResponseDto;
import com.CardMaster.mapper.cias.CardAccountMapper;
import com.CardMaster.model.cias.CardAccount;
import com.CardMaster.Enum.cias.AccountStatus;
import com.CardMaster.service.cias.AccountSetupService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AccountSetupControllerTest {

    @Mock
    private AccountSetupService accountService;

    @Mock
    private CardAccountMapper accountMapper;

    @InjectMocks
    private AccountSetupController accountController;

    private CardAccount account;
    private CardAccountResponseDto responseDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        account = new CardAccount();
        account.setAccountId(10L);
        account.setCreditLimit(50000.0);
        account.setAvailableLimit(50000.0);
        account.setOpenDate(LocalDate.now());
        account.setStatus(AccountStatus.ACTIVE);

        responseDto = new CardAccountResponseDto();
        responseDto.setAccountId(10L);
        responseDto.setCreditLimit(50000.0);
        responseDto.setAvailableLimit(50000.0);
        responseDto.setStatus("ACTIVE");
    }

    @Test
    void testCreateAccount() {
        CardAccountRequestDto requestDto = new CardAccountRequestDto();
        requestDto.setCardId(1L);

        when(accountService.createAccount(requestDto)).thenReturn(account);
        when(accountMapper.toDTO(account)).thenReturn(responseDto);

        ResponseEntity<CardAccountResponseDto> result = accountController.createAccount(requestDto);

        assertNotNull(result.getBody());
        assertEquals("ACTIVE", result.getBody().getStatus());
        verify(accountService, times(1)).createAccount(requestDto);
        verify(accountMapper, times(1)).toDTO(account);
    }

    @Test
    void testGetAccount() {
        when(accountService.getAccountById(10L)).thenReturn(account);
        when(accountMapper.toDTO(account)).thenReturn(responseDto);

        ResponseEntity<CardAccountResponseDto> result = accountController.getAccount(10L);

        assertEquals(10L, result.getBody().getAccountId());
        assertEquals("ACTIVE", result.getBody().getStatus());
        verify(accountService, times(1)).getAccountById(10L);
    }

    @Test
    void testUseCard() {
        account.setAvailableLimit(45000.0);
        responseDto.setAvailableLimit(45000.0);

        when(accountService.useCard(10L, 5000.0)).thenReturn(account);
        when(accountMapper.toDTO(account)).thenReturn(responseDto);

        ResponseEntity<CardAccountResponseDto> result = accountController.useCard(10L, 5000.0);

        assertEquals(45000.0, result.getBody().getAvailableLimit());
        verify(accountService, times(1)).useCard(10L, 5000.0);
    }
}
