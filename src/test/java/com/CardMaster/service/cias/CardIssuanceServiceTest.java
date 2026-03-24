package com.CardMaster.service.cias;

import com.CardMaster.Enum.cias.CardStatus;
import com.CardMaster.Enum.cau.UnderwritingDecisionType;
import com.CardMaster.dao.cau.UnderwritingDecisionRepository;
import com.CardMaster.dao.cias.CardRepository;
import com.CardMaster.dao.paa.CardApplicationRepository;
import com.CardMaster.dto.cias.CardRequestDto;
import com.CardMaster.model.cau.UnderwritingDecision;
import com.CardMaster.model.cias.Card;
import com.CardMaster.model.cpl.CardProduct;
import com.CardMaster.model.paa.CardApplication;
import com.CardMaster.model.paa.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CardIssuanceServiceTest {

    @Mock
    private CardRepository cardRepository;

    @Mock
    private CardApplicationRepository applicationRepository;

    @Mock
    private UnderwritingDecisionRepository decisionRepository;

    @InjectMocks
    private CardIssuanceService cardService;

    private CardRequestDto requestDto;
    private Card card;
    private CardApplication application;
    private UnderwritingDecision decision;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        requestDto = new CardRequestDto();
        requestDto.setApplicationId(11L);
        requestDto.setMaskedCardNumber("**** **** **** 1234");
        requestDto.setExpiryDate(LocalDate.of(2030, 12, 31));
        requestDto.setCvvHash("hashed-cvv");

        Customer customer = new Customer();
        customer.setCustomerId(1L);

        CardProduct product = new CardProduct();
        product.setProductId(1L);

        application = new CardApplication();
        application.setApplicationId(11L);
        application.setCustomer(customer);
        application.setProduct(product);
        application.setStatus(CardApplication.CardApplicationStatus.Approved);

        decision = new UnderwritingDecision();
        decision.setDecision(UnderwritingDecisionType.APPROVE);
        decision.setApprovedLimit(75000.0);

        card = new Card();
        card.setCardId(1L);
        card.setApplication(application);
        card.setCustomer(customer);
        card.setProduct(product);
        card.setMaskedCardNumber("**** **** **** 1234");
        card.setExpiryDate(LocalDate.of(2030, 12, 31));
        card.setStatus(CardStatus.ISSUED);
    }

    @Test
    void testCreateCard() {
        when(applicationRepository.findById(11L)).thenReturn(Optional.of(application));
        when(decisionRepository.findTopByApplication_ApplicationIdOrderByDecisionDateDesc(11L)).thenReturn(Optional.of(decision));
        when(cardRepository.existsByApplicationApplicationId(11L)).thenReturn(false);
        when(cardRepository.save(any(Card.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Card result = cardService.createCard(requestDto);

        assertNotNull(result);
        assertEquals(CardStatus.ISSUED, result.getStatus());
        assertEquals(application, result.getApplication());
        assertEquals(application.getCustomer(), result.getCustomer());
        verify(cardRepository, times(1)).save(any(Card.class));
    }

    @Test
    void testGetCardById() {
        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));

        Card result = cardService.getCardById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getCardId());
        verify(cardRepository, times(1)).findById(1L);
    }

    @Test
    void testBlockCard() {
        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));
        when(cardRepository.save(card)).thenReturn(card);

        Card result = cardService.blockCard(1L);

        assertEquals(CardStatus.BLOCKED, result.getStatus());
        verify(cardRepository, times(1)).save(card);
    }

    @Test
    void testCreateCardRequiresApprovedApplication() {
        application.setStatus(CardApplication.CardApplicationStatus.UnderReview);
        when(applicationRepository.findById(11L)).thenReturn(Optional.of(application));

        assertThrows(IllegalStateException.class, () -> cardService.createCard(requestDto));
    }
}
