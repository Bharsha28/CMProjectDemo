package com.CardMaster.controller.tap;

import com.CardMaster.dto.tap.TransactionDto;
import com.CardMaster.mapper.tap.TransactionMapper;
import com.CardMaster.model.tap.Transaction;
import com.CardMaster.service.tap.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService service;
    private final TransactionMapper mapper;

    /**
     * Authorize a new transaction (creates a hold and sets status=AUTHORIZED).
     * Accepts TransactionDto to keep API decoupled from JPA entity.
     */
    @PostMapping("/authorize")
    @PreAuthorize("hasAnyRole('CUSTOMER','ADMIN')")
    public ResponseEntity<TransactionDto> authorize(@RequestBody TransactionDto dto) {
        Transaction entity = mapper.toEntity(dto);
        Transaction saved = service.authorize(entity);
        return ResponseEntity.ok(mapper.toDTO(saved));
    }

    /**
     * Post (capture) a previously authorized transaction by its ID.
     */
    @PostMapping("/post/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','OFFICER')")
    public ResponseEntity<TransactionDto> post(@PathVariable Long id) {
        TransactionDto posted = service.post(id);
        return ResponseEntity.ok(posted);
    }

    /**
     * Reverse a transaction (void an auth or reverse a posted transaction).
     */
    @PostMapping("/reverse/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','OFFICER','RISK')")
    public ResponseEntity<TransactionDto> reverse(@PathVariable Long id) {
        Transaction reversed = service.reverse(id);
        return ResponseEntity.ok(mapper.toDTO(reversed));
    }

    /**
     * Get a transaction by ID.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('CUSTOMER','ADMIN','OFFICER','RISK')")
    public ResponseEntity<TransactionDto> get(@PathVariable Long id) {
        Transaction tx = service.getById(id);
        return ResponseEntity.ok(mapper.toDTO(tx));
    }

    /**
     * List all transactions (basic, no filters for now).
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('CUSTOMER','ADMIN','OFFICER','RISK')")
    public ResponseEntity<List<TransactionDto>> list() {
        List<Transaction> all = service.listAll();
        return ResponseEntity.ok(all.stream().map(mapper::toDTO).toList());
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<List<TransactionDto>> listMy(Principal principal) {
        List<Transaction> my = service.listByEmail(principal.getName());
        return ResponseEntity.ok(my.stream().map(mapper::toDTO).toList());
    }
}
