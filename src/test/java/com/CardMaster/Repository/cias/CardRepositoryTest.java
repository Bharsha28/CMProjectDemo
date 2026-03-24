package com.CardMaster.Repository.cias;

import com.CardMaster.dao.cias.CardRepository;
import com.CardMaster.model.cias.Card;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CardRepositoryTest {

    @Mock
    private CardRepository cardRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindByCustomerCustomerId() {
        Card card1 = new Card();
        card1.setCardId(1L);
        Card card2 = new Card();
        card2.setCardId(2L);

        when(cardRepository.findByCustomerCustomerId(100L))
                .thenReturn(Arrays.asList(card1, card2));

        List<Card> cards = cardRepository.findByCustomerCustomerId(100L);

        assertEquals(2, cards.size());
        verify(cardRepository).findByCustomerCustomerId(100L);
    }

    @Test
    void testFindByProductProductId() {
        Card card = new Card();
        card.setCardId(3L);

        when(cardRepository.findByProductProductId(200L))
                .thenReturn(Arrays.asList(card));

        List<Card> cards = cardRepository.findByProductProductId(200L);

        assertEquals(1, cards.size());
        assertEquals(3L, cards.get(0).getCardId());
        verify(cardRepository).findByProductProductId(200L);
    }

    @Test
    void testFindByMaskedCardNumber() {
        Card card = new Card();
        card.setCardId(4L);

        when(cardRepository.findByMaskedCardNumber("XXXX-1234"))
                .thenReturn(card);

        Card result = cardRepository.findByMaskedCardNumber("XXXX-1234");

        assertNotNull(result);
        assertEquals(4L, result.getCardId());
        verify(cardRepository).findByMaskedCardNumber("XXXX-1234");
    }
}
