package com.CardMaster.dao.cau;

import com.CardMaster.model.cau.CreditScore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CreditScoreRepositoryTest {

    @Mock
    private CreditScoreRepository creditScoreRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findLatestScore_returnsValue() {

        CreditScore score = new CreditScore();
        score.setScoreId(1L);
        score.setBureauScore(750);
        score.setInternalScore(690);
        score.setGeneratedDate(LocalDateTime.now());

        when(creditScoreRepository
                .findTopByApplication_ApplicationIdOrderByGeneratedDateDesc(10L))
                .thenReturn(Optional.of(score));


        Optional<CreditScore> result =
                creditScoreRepository.findTopByApplication_ApplicationIdOrderByGeneratedDateDesc(10L);


        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getScoreId());
        assertEquals(750, result.get().getBureauScore());
        assertEquals(690, result.get().getInternalScore());
        verify(creditScoreRepository, times(1))
                .findTopByApplication_ApplicationIdOrderByGeneratedDateDesc(10L);
    }

    @Test
    void findLatestScore_returnsEmpty() {

        when(creditScoreRepository
                .findTopByApplication_ApplicationIdOrderByGeneratedDateDesc(10L))
                .thenReturn(Optional.empty());


        Optional<CreditScore> result =
                creditScoreRepository.findTopByApplication_ApplicationIdOrderByGeneratedDateDesc(10L);


        assertFalse(result.isPresent());
        verify(creditScoreRepository, times(1))
                .findTopByApplication_ApplicationIdOrderByGeneratedDateDesc(10L);
    }
}
