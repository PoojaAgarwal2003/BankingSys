package com.nab.account_service.controller;

import com.nab.account_service.enums.KYCStatus;
import com.nab.account_service.model.Account;
import com.nab.account_service.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @GetMapping("/me")
    public ResponseEntity<List<Account>> getMyAccounts() {
        String email = getCurrentUserEmail();
        List<Account> accounts = accountService.getAccountsForEmail(email);
        return ResponseEntity.ok(accounts);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Account> getAccountById(@PathVariable Long id) {
        String email = getCurrentUserEmail();
        return accountService.getAccountByIdAndEmail(id, email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Account creation is now handled through user registration
    @PostMapping("/create")
    public ResponseEntity<String> createAccount() {
        return ResponseEntity.badRequest()
            .body("Accounts can only be created through user registration");
    }

    @PutMapping("/{id}")
    public ResponseEntity<Account> updateAccount(@PathVariable Long id, @RequestBody Account account) {
        return accountService.getAccountByIdAndEmail(id, getCurrentUserEmail())
                .map(existingAccount -> {
                    account.setId(id);
                    return ResponseEntity.ok(accountService.updateAccount(account));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccount(@PathVariable Long id) {
        String email = getCurrentUserEmail();
        boolean closed = accountService.closeAccount(id, email);
        if (closed) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    private String getCurrentUserEmail() {
        return org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getName();
    }

    @GetMapping("/{id}/kyc-status")
    public ResponseEntity<KYCStatus> getKycStatus(@PathVariable Long id) {
    return accountService.getAccountByIdAndEmail(id, getCurrentUserEmail())
        .map(account -> ResponseEntity.ok(account.getKycStatus()))
        .orElse(ResponseEntity.notFound().build());
    }
}
