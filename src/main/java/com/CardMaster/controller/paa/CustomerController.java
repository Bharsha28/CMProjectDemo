package com.CardMaster.controller.paa;

import com.CardMaster.dto.paa.CustomerDto;
import com.CardMaster.service.paa.CustomerService;
import com.CardMaster.dto.paa.ResponseStructure;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService service;

    // --- Create Customer ---
    @PostMapping
    public ResponseEntity<ResponseStructure<CustomerDto>> createCustomer(
            @Valid @RequestBody CustomerDto dto,
            @RequestHeader("Authorization") String token) {

        CustomerDto created = service.createCustomer(dto, token);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ResponseStructure<>("Customer created successfully", created));
    }

    @GetMapping("/my")
    public ResponseEntity<ResponseStructure<CustomerDto>> getMyCustomer(
            java.security.Principal principal,
            @RequestHeader("Authorization") String token) {

        CustomerDto customer = service.getCustomerByEmail(principal.getName(), token);
        return ResponseEntity.ok(new ResponseStructure<>("Customer profile retrieved successfully", customer));
    }

    // --- Get Customer by ID ---
    @GetMapping("/{id}")
    public ResponseEntity<ResponseStructure<CustomerDto>> getCustomer(
            @PathVariable("id") Long id,
            @RequestHeader("Authorization") String token) {

        CustomerDto customer = service.getCustomer(id, token);
        return ResponseEntity.ok(new ResponseStructure<>("Customer retrieved successfully", customer));
    }

    // --- Get All Customers ---
    @GetMapping
    public ResponseEntity<ResponseStructure<List<CustomerDto>>> getAllCustomers(
            @RequestHeader("Authorization") String token) {

        List<CustomerDto> customers = service.getAllCustomers(token);
        return ResponseEntity.ok(new ResponseStructure<>("Customers retrieved successfully", customers));
    }

    // --- Update Customer ---
    @PutMapping("/{id}")
    public ResponseEntity<ResponseStructure<CustomerDto>> updateCustomer(
            @PathVariable("id") Long id,
            @Valid @RequestBody CustomerDto dto,
            @RequestHeader("Authorization") String token) {

        CustomerDto updated = service.updateCustomer(id, dto, token);
        return ResponseEntity.ok(new ResponseStructure<>("Customer updated successfully", updated));
    }

    // --- Delete Customer ---
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseStructure<Void>> deleteCustomer(
            @PathVariable("id") Long id,
            @RequestHeader("Authorization") String token) {

        service.deleteCustomer(id, token);
        return ResponseEntity.ok(new ResponseStructure<>("Customer deleted successfully", null));
    }

    @PutMapping("/my")
    public ResponseEntity<ResponseStructure<CustomerDto>> updateMyCustomer(
            Principal principal,
            @Valid @RequestBody CustomerDto dto,
            @RequestHeader("Authorization") String token) {

        CustomerDto updated = service.updateCustomerByEmail(principal.getName(), dto, token);
        return ResponseEntity.ok(new ResponseStructure<>("Customer profile updated successfully", updated));
    }
}
