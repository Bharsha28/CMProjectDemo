package com.CardMaster.controller.tap;

import com.CardMaster.dto.tap.TransactionHoldDto;
import com.CardMaster.mapper.tap.TransactionHoldMapper;
import com.CardMaster.model.tap.TransactionHold;
import com.CardMaster.service.tap.TransactionHoldService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/transaction-holds")
@RequiredArgsConstructor
public class TransactionHoldController {

    private final TransactionHoldService service;
    private final TransactionHoldMapper mapper;

    /**
     * Create a hold explicitly (rare in normal flow since authorize creates a hold).
     * Provided for completeness / testing.
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','OFFICER')")
    public ResponseEntity<TransactionHoldDto> create(@RequestBody TransactionHoldDto dto) {
        TransactionHold entity = mapper.toEntity(dto);
        // You might have a service.create(entity) — if not, reuse save logic where appropriate
        TransactionHold saved = service.create(entity);
        return ResponseEntity.ok(mapper.toDTO(saved));
    }

    /**
     * Get a hold by ID.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','OFFICER','RISK')")
    public ResponseEntity<TransactionHoldDto> get(@PathVariable Long id) {
        TransactionHold hold = service.getById(id);
        return ResponseEntity.ok(mapper.toDTO(hold));
    }

    /**
     * List all holds linked to a specific transaction.
     */
    @GetMapping("/by-transaction/{transactionId}")
    @PreAuthorize("hasAnyRole('ADMIN','OFFICER','RISK')")
    public ResponseEntity<List<TransactionHoldDto>> listByTransaction(@PathVariable Long transactionId) {
        List<TransactionHold> holds = service.listByTransaction(transactionId);
        return ResponseEntity.ok(holds.stream().map(mapper::toDTO).toList());
    }

    /**
     * Release a hold (sets releaseDate and frees amount).
     */
    @PostMapping("/release/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','OFFICER','RISK')")
    public ResponseEntity<TransactionHoldDto> release(@PathVariable Long id) {
        TransactionHold released = service.release(id);
        return ResponseEntity.ok(mapper.toDTO(released));
    }
}
