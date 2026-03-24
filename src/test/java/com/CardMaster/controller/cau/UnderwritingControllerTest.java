package com.CardMaster.controller.cau;

import com.CardMaster.dto.cau.CreditScoreGenerateRequest;
import com.CardMaster.dto.cau.CreditScoreResponse;
import com.CardMaster.dto.cau.UnderwritingDecisionRequest;
import com.CardMaster.dto.cau.UnderwritingDecisionResponse;
import com.CardMaster.exceptions.cau.EntityNotFoundException;
import com.CardMaster.exceptions.cau.ValidationException;
import com.CardMaster.exceptions.cau.UnauthorizedActionException;
import com.CardMaster.service.cau.UnderwritingService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UnderwritingControllerTest {

    @Mock
    private UnderwritingService service;

    @InjectMocks
    private UnderwritingController controller;

    private CreditScoreResponse scoreResponse;
    private UnderwritingDecisionResponse decisionResponse;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Prepare score response
        scoreResponse = new CreditScoreResponse();
        scoreResponse.setBureauScore(750);
        scoreResponse.setInternalScore(690);

        // Prepare decision response
        decisionResponse = new UnderwritingDecisionResponse();
        decisionResponse.setApprovedLimit(50000.0);
        decisionResponse.setRemarks("Approved");
    }

    // ---------------- CREATE SCORE ----------------

    @Test
    void testCreateScore_success() {
        CreditScoreGenerateRequest req = new CreditScoreGenerateRequest();
        req.setBureauScore(750);

        when(service.generateScore(1L, req)).thenReturn(scoreResponse);

        ResponseEntity<CreditScoreResponse> result = controller.createScore(1L, req);

        assertNotNull(result.getBody());
        assertEquals(750, result.getBody().getBureauScore());
        assertEquals(690, result.getBody().getInternalScore());
        verify(service, times(1)).generateScore(1L, req);
    }

    @Test
    void testCreateScore_validationError() {
        CreditScoreGenerateRequest req = new CreditScoreGenerateRequest();
        req.setBureauScore(500);

        when(service.generateScore(1L, req))
                .thenThrow(new ValidationException("Invalid score"));

        assertThrows(
                ValidationException.class,
                () -> controller.createScore(1L, req)
        );
    }

    // ---------------- GET LATEST SCORE ----------------

    @Test
    void testGetLatestScore_success() {
        when(service.getLatestScore(1L)).thenReturn(scoreResponse);

        ResponseEntity<CreditScoreResponse> result = controller.getLatestScore(1L);

        assertEquals(750, result.getBody().getBureauScore());
        verify(service, times(1)).getLatestScore(1L);
    }

    @Test
    void testGetLatestScore_notFound() {
        when(service.getLatestScore(1L))
                .thenThrow(new EntityNotFoundException("CreditScore", 1L));

        assertThrows(
                EntityNotFoundException.class,
                () -> controller.getLatestScore(1L)
        );
    }

    // ---------------- CREATE DECISION ----------------

    @Test
    void testCreateDecision_success() {
        UnderwritingDecisionRequest req = new UnderwritingDecisionRequest();
        req.setApprovedLimit(50000.0);

        when(service.createDecision(1L, req, "Bearer x")).thenReturn(decisionResponse);

        ResponseEntity<UnderwritingDecisionResponse> result =
                controller.createDecision(1L, req, "Bearer x");

        assertNotNull(result.getBody());
        assertEquals(50000.0, result.getBody().getApprovedLimit());
        verify(service, times(1)).createDecision(1L, req, "Bearer x");
    }

    @Test
    void testCreateDecision_unauthorized() {
        UnderwritingDecisionRequest req = new UnderwritingDecisionRequest();

        when(service.createDecision(1L, req, "Bearer x"))
                .thenThrow(new UnauthorizedActionException("Not allowed"));

        assertThrows(
                UnauthorizedActionException.class,
                () -> controller.createDecision(1L, req, "Bearer x")
        );
    }

    // ---------------- GET LATEST DECISION ----------------

    @Test
    void testGetLatestDecision_success() {
        when(service.getLatestDecision(1L)).thenReturn(decisionResponse);

        ResponseEntity<UnderwritingDecisionResponse> result =
                controller.getLatestDecision(1L);

        assertEquals(50000.0, result.getBody().getApprovedLimit());
        verify(service, times(1)).getLatestDecision(1L);
    }

    @Test
    void testGetLatestDecision_notFound() {
        when(service.getLatestDecision(1L))
                .thenThrow(new EntityNotFoundException("UnderwritingDecision", 1L));

        assertThrows(
                EntityNotFoundException.class,
                () -> controller.getLatestDecision(1L)
        );
    }
}