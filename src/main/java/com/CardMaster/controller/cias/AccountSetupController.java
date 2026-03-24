package com.CardMaster.controller.cias;

import com.CardMaster.dto.cias.CardAccountRequestDto;
import com.CardMaster.dto.cias.CardAccountResponseDto;
import com.CardMaster.mapper.cias.CardAccountMapper;
import com.CardMaster.model.cias.CardAccount;
import com.CardMaster.service.cias.AccountSetupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountSetupController {

    private final AccountSetupService accountService;
    private final CardAccountMapper accountMapper;

    @GetMapping
    public ResponseEntity<List<CardAccountResponseDto>> getAllAccounts() {
        List<CardAccount> accounts = accountService.getAllAccounts();
        return ResponseEntity.ok(accounts.stream()
                .map(accountMapper::toDTO)
                .collect(Collectors.toList()));
    }

    @PostMapping
    public ResponseEntity<CardAccountResponseDto> createAccount(@RequestBody CardAccountRequestDto request) {
        CardAccount account = accountService.createAccount(request);
        return ResponseEntity.ok(accountMapper.toDTO(account));
    }

    @GetMapping("/{accountId}")
    public ResponseEntity<CardAccountResponseDto> getAccount(@PathVariable("accountId") Long accountId) {
        CardAccount account = accountService.getAccountById(accountId);
        return ResponseEntity.ok(accountMapper.toDTO(account));
    }

    @GetMapping("/my")
    public ResponseEntity<CardAccountResponseDto> getMyAccount(Principal principal) {
        CardAccount account = accountService.getAccountByEmail(principal.getName());
        return ResponseEntity.ok(accountMapper.toDTO(account));
    }


    @PostMapping("/use/{accountId}")
    public ResponseEntity<CardAccountResponseDto> useCard(
            @PathVariable("accountId") Long accountId,
            @RequestParam("amount") Double amount) {
        CardAccount account = accountService.useCard(accountId, amount);
        return ResponseEntity.ok(accountMapper.toDTO(account));
    }
}
