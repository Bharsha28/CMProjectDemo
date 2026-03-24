//package com.CardMaster.controller.cau;
//
//import com.CardMaster.dto.cau.CreditScoreGenerateRequest;
//import com.CardMaster.dto.cau.CreditScoreResponse;
//import com.CardMaster.dto.cau.UnderwritingDecisionRequest;
//import com.CardMaster.dto.cau.UnderwritingDecisionResponse;
//
//import com.CardMaster.mapper.cau.UnderwritingMapper;
//import com.CardMaster.model.cau.CreditScore;
//import com.CardMaster.model.cau.UnderwritingDecision;
//import com.CardMaster.service.cau.UnderwritingService;
//
//import jakarta.validation.Valid;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//
//@RestController
//@RequestMapping("/applications/{appId}")
//@RequiredArgsConstructor
//public class UnderwritingController {
//
//    private final UnderwritingService service;
//    private final UnderwritingMapper mapper;
//
//    /**
//     * Create (generate) a credit score for the application.
//     * POST /applications/{appId}/scores
//     * Body: CreditScoreGenerateRequest
//     * Returns: CreditScoreResponse
//     */
//    @PostMapping("/scores")
//    public ResponseEntity<CreditScoreResponse> createScore(
//            @PathVariable Long appId,
//            @Valid @RequestBody CreditScoreGenerateRequest req) {
//
//        CreditScore entity = service.generateScore(appId, req);
//        CreditScoreResponse resDto = mapper.toCreditScoreResponse(entity);
//        return ResponseEntity.status(HttpStatus.CREATED).body(resDto);
//    }
//
//    /**
//     * Get the latest credit score for the application.
//     * GET /applications/{appId}/scores/latest
//     * Returns: CreditScoreResponse
//     */
//    @GetMapping("/scores/latest")
//    public ResponseEntity<CreditScoreResponse> getLatestScore(@PathVariable Long appId) {
//        CreditScore entity = service.getLatestScore(appId);
//        CreditScoreResponse resDto = mapper.toCreditScoreResponse(entity);
//        return ResponseEntity.ok(resDto);
//    }
//
//    /**
//     * Create an underwriting decision.
//     * The underwriter is derived from the JWT subject (email/userId) inside the service.
//     * POST /applications/{appId}/decisions
//     * Body: UnderwritingDecisionRequest
//     * Header: Authorization: Bearer <jwt>
//     * Returns: UnderwritingDecisionResponse
//     */
//    @PostMapping("/decisions")
//    public ResponseEntity<UnderwritingDecisionResponse> createDecision(
//            @PathVariable Long appId,
//            @Valid @RequestBody UnderwritingDecisionRequest req,
//            @RequestHeader("Authorization") String authorization) {
//
//        UnderwritingDecision entity = service.createDecision(appId, req, authorization);
//        UnderwritingDecisionResponse resDto = mapper.toUnderwritingDecisionResponse(entity);
//        return ResponseEntity.status(HttpStatus.CREATED).body(resDto);
//    }
//
//    /**
//     * Get the latest underwriting decision for the application.
//     * GET /applications/{appId}/decisions/latest
//     * Returns: UnderwritingDecisionResponse
//     */
//    @GetMapping("/decisions/latest")
//    public ResponseEntity<UnderwritingDecisionResponse> getLatestDecision(@PathVariable Long appId) {
//        UnderwritingDecision entity = service.getLatestDecision(appId);
//        UnderwritingDecisionResponse resDto = mapper.toUnderwritingDecisionResponse(entity);
//        return ResponseEntity.ok(resDto);
//    }
//}
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

/**
 * Module-3: Credit Assessment & Underwriting (Option A)
 * - Service returns DTOs (service uses mapper.to...Response internally).
 * - Controller is minimal: forwards to service and returns DTOs.
 * - Underwriter is derived from JWT subject (numeric userId) inside service.
 */
@RestController
@RequestMapping("/applications/{appId}")
@RequiredArgsConstructor
public class UnderwritingController {

    private final UnderwritingService service;

    /**
     * Create (generate) a credit score for the application.
     * POST /applications/{appId}/scores
     * Body: CreditScoreGenerateRequest
     * Returns: CreditScoreResponse
     */
    @PostMapping("/scores")
    public ResponseEntity<CreditScoreResponse> createScore(
            @PathVariable Long appId,
            @Valid @RequestBody CreditScoreGenerateRequest req) {

        CreditScoreResponse dto = service.generateScore(appId, req);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    /**
     * Get the latest credit score for the application.
     * GET /applications/{appId}/scores/latest
     * Returns: CreditScoreResponse
     */
    @GetMapping("/scores/latest")
    public ResponseEntity<CreditScoreResponse> getLatestScore(@PathVariable Long appId) {
        CreditScoreResponse dto = service.getLatestScore(appId);
        return ResponseEntity.ok(dto);
    }

    /**
     * Create an underwriting decision (underwriter from JWT userId).
     * POST /applications/{appId}/decisions
     * Body: UnderwritingDecisionRequest
     * Header: Authorization: Bearer <jwt>
     * Returns: UnderwritingDecisionResponse
     */
    @PostMapping("/decisions")
    public ResponseEntity<UnderwritingDecisionResponse> createDecision(
            @PathVariable Long appId,
            @Valid @RequestBody UnderwritingDecisionRequest req,
            @RequestHeader("Authorization") String authorization) {

        UnderwritingDecisionResponse dto = service.createDecision(appId, req, authorization);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    /**
     * Get the latest underwriting decision for the application.
     * GET /applications/{appId}/decisions/latest
     * Returns: UnderwritingDecisionResponse
     */
    @GetMapping("/decisions/latest")
    public ResponseEntity<UnderwritingDecisionResponse> getLatestDecision(@PathVariable Long appId) {
        UnderwritingDecisionResponse dto = service.getLatestDecision(appId);
        return ResponseEntity.ok(dto);
    }
}