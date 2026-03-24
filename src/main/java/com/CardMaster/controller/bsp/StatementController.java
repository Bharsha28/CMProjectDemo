package com.CardMaster.controller.bsp;

import com.CardMaster.dto.bsp.StatementDto;
import com.CardMaster.mapper.bsp.StatementMapper;
import com.CardMaster.model.bsp.Statement;
import com.CardMaster.service.bsp.StatementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/billing/statements")
@RequiredArgsConstructor
public class StatementController {

    private final StatementService service;
    private final StatementMapper mapper;

    /**
     * Generate a statement for an account & period.
     * The service will compute totals (totalDue/minimumDue) based on posted transactions.
     */
    @PostMapping("/generate")
    @PreAuthorize("hasAnyRole('ADMIN','OFFICER')")
    public ResponseEntity<StatementDto> generate(@RequestBody StatementDto dto) {
        Statement input = mapper.toEntity(dto);
        Statement generated = service.generateStatement(input);
        return ResponseEntity.ok(mapper.toDTO(generated));
    }

    /**
     * Close a statement (e.g., after due date or when payment cycle ends).
     */
    @PostMapping("/close/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','OFFICER')")
    public ResponseEntity<StatementDto> close(@PathVariable Long id) {
        Statement closed = service.closeStatement(id);
        return ResponseEntity.ok(mapper.toDTO(closed));
    }

    /**
     * Get a statement by ID.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('CUSTOMER','ADMIN','OFFICER')")
    public ResponseEntity<StatementDto> get(@PathVariable Long id) {
        Statement st = service.getById(id);
        return ResponseEntity.ok(mapper.toDTO(st));
    }

    /**
     * List all statements (basic; add filters later if needed).
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('CUSTOMER','ADMIN','OFFICER')")
    public ResponseEntity<List<StatementDto>> list() {
        List<Statement> list = service.listAll();
        return ResponseEntity.ok(list.stream().map(mapper::toDTO).toList());
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<List<StatementDto>> listMy(Principal principal) {
        List<Statement> list = service.listByEmail(principal.getName());
        return ResponseEntity.ok(list.stream().map(mapper::toDTO).toList());
    }
}
