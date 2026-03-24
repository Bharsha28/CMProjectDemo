package com.CardMaster.controller.cau;

import com.CardMaster.dto.cau.CreditScoreGenerateRequest;
import com.CardMaster.dto.cau.CreditScoreResponse;
import com.CardMaster.dto.cau.UnderwritingDecisionRequest;
import com.CardMaster.dto.cau.UnderwritingDecisionResponse;
import com.CardMaster.service.cau.UnderwritingService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UnderwritingController {

    private final UnderwritingService service;

    @PostMapping("/applications/{appId}/scores")
    public ResponseEntity<CreditScoreResponse> createScore(
            @PathVariable("appId") Long appId,
            @Valid @RequestBody CreditScoreGenerateRequest req) {

        CreditScoreResponse dto = service.generateScore(appId, req);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @GetMapping("/applications/{appId}/scores/latest")
    public ResponseEntity<CreditScoreResponse> getLatestScore(@PathVariable("appId") Long appId) {
        CreditScoreResponse dto = service.getLatestScore(appId);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/applications/{appId}/decisions")
    public ResponseEntity<UnderwritingDecisionResponse> createDecision(
            @PathVariable("appId") Long appId,
            @Valid @RequestBody UnderwritingDecisionRequest req,
            @RequestHeader("Authorization") String authorization) {

        UnderwritingDecisionResponse dto = service.createDecision(appId, req, authorization);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @GetMapping("/applications/{appId}/decisions/latest")
    public ResponseEntity<UnderwritingDecisionResponse> getLatestDecision(@PathVariable("appId") Long appId) {
        UnderwritingDecisionResponse dto = service.getLatestDecision(appId);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/scores")
    public ResponseEntity<List<CreditScoreResponse>> getAllRecentScores() {
        return ResponseEntity.ok(service.getRecentScores());
    }

    @GetMapping("/decisions")
    public ResponseEntity<List<UnderwritingDecisionResponse>> getAllRecentDecisions() {
        return ResponseEntity.ok(service.getRecentDecisions());
    }
}