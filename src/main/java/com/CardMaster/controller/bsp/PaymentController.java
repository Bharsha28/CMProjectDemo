package com.CardMaster.controller.bsp;

import com.CardMaster.dto.bsp.PaymentDto;
import com.CardMaster.mapper.bsp.PaymentMapper;
import com.CardMaster.model.bsp.Payment;
import com.CardMaster.service.bsp.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/billing/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService service;
    private final PaymentMapper mapper;

    /**
     * Capture (record) a payment for an account.
     * The service should update statement dues and account balances accordingly.
     */
    @PostMapping("/capture")
    @PreAuthorize("hasAnyRole('CUSTOMER','ADMIN','OFFICER')")
    public ResponseEntity<PaymentDto> capture(@RequestBody PaymentDto dto) {
        Payment input = mapper.toEntity(dto);
        Payment saved = service.capturePayment(input);
        return ResponseEntity.ok(mapper.toDTO(saved));
    }

    /**
     * Get a payment by ID.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('CUSTOMER','ADMIN','OFFICER')")
    public ResponseEntity<PaymentDto> get(@PathVariable Long id) {
        Payment p = service.getById(id);
        return ResponseEntity.ok(mapper.toDTO(p));
    }

    /**
     * List all payments.
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('CUSTOMER','ADMIN','OFFICER')")
    public ResponseEntity<List<PaymentDto>> list() {
        List<Payment> list = service.listAll();
        return ResponseEntity.ok(list.stream().map(mapper::toDTO).toList());
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<List<PaymentDto>> listMy(Principal principal) {
        List<Payment> list = service.listByEmail(principal.getName());
        return ResponseEntity.ok(list.stream().map(mapper::toDTO).toList());
    }
}
