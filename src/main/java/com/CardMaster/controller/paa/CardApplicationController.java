package com.CardMaster.controller.paa;

import com.CardMaster.dto.paa.CardApplicationDto;
import com.CardMaster.dto.paa.ResponseStructure;
import com.CardMaster.service.paa.CardApplicationService;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
public class CardApplicationController {

    private static final Logger log = LogManager.getLogger(CardApplicationController.class);
    private final CardApplicationService applicationService;

    // --- Create Application ---
    @PostMapping
    public ResponseEntity<ResponseStructure<CardApplicationDto>> create(
            @Valid @RequestBody CardApplicationDto dto,
            @RequestHeader("Authorization") String token) {

        log.info("Creating card application for customer {}", dto.getCustomerId());
        CardApplicationDto saved = applicationService.create(dto, token);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ResponseStructure<>("Application created successfully", saved));
    }

    @GetMapping("/my")
    public ResponseEntity<ResponseStructure<List<CardApplicationDto>>> getMyApplications(
            java.security.Principal principal,
            @RequestHeader("Authorization") String token) {

        log.info("Fetching applications for current user: {}", principal.getName());
        List<CardApplicationDto> apps = applicationService.getApplicationsForUserEmail(principal.getName());
        return ResponseEntity.ok(new ResponseStructure<>("Your applications retrieved successfully", apps));
    }

    // --- Get Application by ID ---
    @GetMapping("/{id}")
    public ResponseEntity<ResponseStructure<CardApplicationDto>> getById(
            @PathVariable("id") Long id,
            @RequestHeader("Authorization") String token) {

        log.info("Fetching application {}", id);
        CardApplicationDto dto = applicationService.findById(id, token);
        return ResponseEntity.ok(new ResponseStructure<>("Application retrieved successfully", dto));
    }

    // --- Get All Applications ---
    @GetMapping
    public ResponseEntity<ResponseStructure<List<CardApplicationDto>>> getAllApplications(
            @RequestHeader("Authorization") String token) {

        log.info("Fetching all applications");
        List<CardApplicationDto> apps = applicationService.getAllApplications(token);
        return ResponseEntity.ok(new ResponseStructure<>("All applications retrieved successfully", apps));
    }

    // --- Get Applications by Customer ---
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<ResponseStructure<List<CardApplicationDto>>> getByCustomer(
            @PathVariable("customerId") Long customerId,
            @RequestHeader("Authorization") String token) {

        log.info("Fetching applications for customer {}", customerId);
        List<CardApplicationDto> apps = applicationService.getApplicationsByCustomer(customerId, token);

        if (apps == null || apps.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseStructure<>("Customer doesn't have any applications", null));
        }

        return ResponseEntity.ok(new ResponseStructure<>("Applications retrieved successfully", apps));
    }

    @GetMapping("/{id}/status")
    public ResponseEntity<ResponseStructure<Object>> getStatus(
            @PathVariable("id") Long id,
            @RequestHeader("Authorization") String token) {

        log.info("Fetching status for application {}", id);
        CardApplicationDto dto = applicationService.findById(id, token);

        // Build a small response object with just id + status
        Map<String, Object> statusResponse = new HashMap<>();
        statusResponse.put("applicationId", dto.getApplicationId());
        statusResponse.put("status", dto.getStatus());

        return ResponseEntity.ok(
                new ResponseStructure<>("Application status retrieved successfully", statusResponse)
        );
    }

    // --- Update Application Status ---
    @PutMapping("/{id}/status")
    public ResponseEntity<ResponseStructure<CardApplicationDto>> updateStatus(
            @PathVariable("id") Long id,
            @RequestParam("status") String status,
            @RequestHeader("Authorization") String token) {

        log.info("Updating status for application {} to {}", id, status);
        CardApplicationDto updated = applicationService.updateApplicationStatus(id, status, token);
        return ResponseEntity.ok(new ResponseStructure<>("Application status updated successfully", updated));
    }


}
