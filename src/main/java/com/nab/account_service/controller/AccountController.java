package com.nab.account_service.controller;

import com.nab.account_service.enums.KYCStatus;
import com.nab.account_service.model.Account;
import com.nab.account_service.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.nab.account_service.repository.AccountRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Map;
@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private AccountService accountService;

    @GetMapping("/me")
    public ResponseEntity<List<Account>> getMyAccounts() {
        String userId = getCurrentUserId();
        List<Account> accounts = accountService.getAccountsForUserId(userId);
        return ResponseEntity.ok(accounts);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Account> getAccountById(@PathVariable Long id) {
        String userId = getCurrentUserId();
        return accountService.getAccountByIdAndUserId(id, userId)
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
        String userId = getCurrentUserId();
        return accountService.getAccountByIdAndUserId(id, userId)
                .map(existingAccount -> {
                    account.setId(id);
                    return ResponseEntity.ok(accountService.updateAccount(account));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccount(@PathVariable Long id) {
        String userId = getCurrentUserId();
        boolean closed = accountService.closeAccount(id, userId);
        if (closed) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    private String getCurrentUserId() {
        return org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getName();
    }
    @GetMapping("/{accountNo}")
    public ResponseEntity<Boolean> checkAccountNo(@PathVariable String accountNo) {
        boolean exists = accountService.accountNoExists(accountNo);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/user/{user_id}/kyc-status")
    public ResponseEntity<KYCStatus> getKycStatus(@PathVariable String user_id) {
    return accountService.getFirstAccountByUserId(user_id)
        .map(account -> ResponseEntity.ok(account.getKycStatus()))
        .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Account>> getAccountsByUserId(@PathVariable String userId) {
        List<Account> accounts = accountService.getAccountsForUserId(userId);
        if (accounts.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(accounts);
    }

    @DeleteMapping("/user/{userId}")
    public ResponseEntity<Void> deleteAccountsByUserId(@PathVariable String userId) {
        boolean closed = accountService.closeAccountsByUserId(userId);
        if (closed) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/accountNo/{accountNo}/status")
    public ResponseEntity<String> getAccountStatusByAccountNo(@PathVariable String accountNo) {
        Optional<Account> account = accountService.getAccountByAccountNo(accountNo);
        if (account.isPresent()) {
            return ResponseEntity.ok(account.get().getAccountStatus().name());
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    @PostMapping("/{accountNo}/balance")
    public ResponseEntity<String> updateBalance(
            @PathVariable String accountNo,
            @RequestBody BalanceUpdateRequest request) {
        boolean success = accountService.updateBalance(accountNo, request.getAmountChange());
        if (success) {
            return ResponseEntity.ok("SUCCESS");
        } else {
            return ResponseEntity.badRequest().body("FAILED");
        }
    }
    public static class BalanceUpdateRequest {
        private BigDecimal amountChange;
        public BigDecimal getAmountChange() { return amountChange; }
        public void setAmountChange(BigDecimal amountChange) { this.amountChange = amountChange; }
    }

    @PatchMapping("/user/{userId}/kyc-status")
public ResponseEntity<?> updateKycStatus(@PathVariable String userId, @RequestBody Map<String, String> body) {
    // Fetch the account by userId
    List<Account> accounts = accountRepository.findByUserId(userId);
    if (accounts.isEmpty()) {
        return ResponseEntity.notFound().build();
    }
    Account account = accounts.get(0);
    // Update the KYC status
    String newStatus = body.get("kycStatus");
    account.setKycStatus(KYCStatus.valueOf(newStatus));
    accountRepository.save(account);
    return ResponseEntity.ok().build();
}
}
