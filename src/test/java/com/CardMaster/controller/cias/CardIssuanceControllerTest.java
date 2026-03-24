package com.CardMaster.controller.cias;

import com.CardMaster.dto.cias.CardRequestDto;
import com.CardMaster.dto.cias.CardResponseDto;
import com.CardMaster.mapper.cias.CardMapper;
import com.CardMaster.model.cias.Card;
import com.CardMaster.Enum.cias.CardStatus;
import com.CardMaster.service.cias.CardIssuanceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CardIssuanceControllerTest {

    @Mock
    private CardIssuanceService cardService;

    @Mock
    private CardMapper cardMapper;

    @InjectMocks
    private CardIssuanceController cardController;

    private Card card;
    private CardResponseDto responseDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        card = new Card();
        card.setCardId(1L);
        card.setStatus(CardStatus.ISSUED);

        responseDto = new CardResponseDto();
        responseDto.setCardId(1L);
        responseDto.setStatus("ISSUED");
    }

    @Test
    void testCreateCard() {
        CardRequestDto requestDto = new CardRequestDto();
        requestDto.setApplicationId(1L);

        when(cardService.createCard(requestDto)).thenReturn(card);
        when(cardMapper.toDTO(card)).thenReturn(responseDto);

        ResponseEntity<CardResponseDto> result = cardController.createCard(requestDto);

        assertNotNull(result.getBody());
        assertEquals("ISSUED", result.getBody().getStatus());
        verify(cardService, times(1)).createCard(requestDto);
        verify(cardMapper, times(1)).toDTO(card);
    }

    @Test
    void testGetCard() {
        when(cardService.getCardById(1L)).thenReturn(card);
        when(cardMapper.toDTO(card)).thenReturn(responseDto);

        ResponseEntity<CardResponseDto> result = cardController.getCard(1L);

        assertEquals(1L, result.getBody().getCardId());
        assertEquals("ISSUED", result.getBody().getStatus());
        verify(cardService, times(1)).getCardById(1L);
    }

    @Test
    void testBlockCard() {
        card.setStatus(CardStatus.BLOCKED);
        responseDto.setStatus("BLOCKED");

        when(cardService.blockCard(1L)).thenReturn(card);
        when(cardMapper.toDTO(card)).thenReturn(responseDto);

        ResponseEntity<CardResponseDto> result = cardController.blockCard(1L);

        assertEquals("BLOCKED", result.getBody().getStatus());
        verify(cardService, times(1)).blockCard(1L);
    }
}
