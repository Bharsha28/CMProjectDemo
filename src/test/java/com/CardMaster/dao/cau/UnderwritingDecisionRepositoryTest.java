package com.CardMaster.dao.cau;

import com.CardMaster.Enum.cau.UnderwritingDecisionType;
import com.CardMaster.model.cau.UnderwritingDecision;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UnderwritingDecisionRepositoryTest {

    @Mock
    private UnderwritingDecisionRepository decisionRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findLatestDecision_returnsValue() {
        // Arrange
        UnderwritingDecision d = new UnderwritingDecision();
        d.setDecisionId(5L);
        d.setDecision(UnderwritingDecisionType.APPROVE);
        d.setApprovedLimit(45000.0);
        d.setRemarks("ok");
        d.setDecisionDate(LocalDateTime.now());

        when(decisionRepository
                .findTopByApplication_ApplicationIdOrderByDecisionDateDesc(20L))
                .thenReturn(Optional.of(d));

        // Act
        Optional<UnderwritingDecision> result =
                decisionRepository.findTopByApplication_ApplicationIdOrderByDecisionDateDesc(20L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(5L, result.get().getDecisionId());
        assertEquals(UnderwritingDecisionType.APPROVE, result.get().getDecision());
        verify(decisionRepository, times(1))
                .findTopByApplication_ApplicationIdOrderByDecisionDateDesc(20L);
    }

    @Test
    void findLatestDecision_returnsEmpty() {
        // Arrange
        when(decisionRepository
                .findTopByApplication_ApplicationIdOrderByDecisionDateDesc(20L))
                .thenReturn(Optional.empty());

        // Act
        Optional<UnderwritingDecision> result =
                decisionRepository.findTopByApplication_ApplicationIdOrderByDecisionDateDesc(20L);

        // Assert
        assertFalse(result.isPresent());
        verify(decisionRepository, times(1))
                .findTopByApplication_ApplicationIdOrderByDecisionDateDesc(20L);
    }
}