package com.nab.account_service.service;

import com.nab.account_service.enums.AccountStatus;
import com.nab.account_service.enums.KYCStatus;
import com.nab.account_service.model.Account;
import com.nab.account_service.model.User;
import com.nab.account_service.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AccountService {
    
    private final AccountRepository accountRepository;
    
    public List<Account> getAccountsForUserId(String userId) {
        return accountRepository.findByUserId(userId);
    }
    
    public Optional<Account> getAccountByIdAndUserId(Long id, String userId) {
        return accountRepository.findByIdAndUserId(id, userId);
    }
    
    @Transactional
    public Account createAccountForUser(User user) {
        Account account = new Account();
        account.setUserId(user.getUserId());
        account.setEmail(user.getEmail());
        account.setAccountNo(generateAccountNumber());
        account.setBalance(user.getCashDeposited());
        account.setAccountStatus(AccountStatus.APPROVED);
        account.setKycStatus(KYCStatus.PENDING); // Explicitly set KYC status to PENDING
        return accountRepository.save(account);
    }
    
    public Account updateAccount(Account account) {
        return accountRepository.save(account);
    }
    
    @Transactional
    public boolean closeAccount(Long id, String userId) {
        Optional<Account> accountOpt = accountRepository.findByIdAndUserId(id, userId);
        if (accountOpt.isPresent()) {
            Account account = accountOpt.get();
            account.setAccountStatus(AccountStatus.CLOSED);
            accountRepository.save(account);
            return true;
        }
        return false;
    }
    public boolean accountNoExists(String accountNo) {
        return accountRepository.findByAccountNo(accountNo).isPresent();
    }

    @Transactional
    public Account updateKycStatus(Long accountId, KYCStatus kycStatus) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        account.setKycStatus(kycStatus);
        return accountRepository.save(account);
    }

    public boolean closeAccountsByUserId(String userId) {
        List<Account> accounts = accountRepository.findByUserId(userId);
        if (accounts.isEmpty()) return false;
        for (Account account : accounts) {
            account.setAccountStatus(AccountStatus.CLOSED);
            accountRepository.save(account);
        }
        return true;
    }

    private String generateAccountNumber() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 12; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

    public Optional<Account> getAccountByAccountNo(String accountNo) {
        return accountRepository.findByAccountNo(accountNo);
    }


    public boolean updateBalance(String accountNo, java.math.BigDecimal amountChange) {
        java.util.Optional<Account> accountOpt = accountRepository.findByAccountNo(accountNo);
        if (accountOpt.isEmpty()) return false;

        Account account = accountOpt.get();
        java.math.BigDecimal currentBalance = java.math.BigDecimal.valueOf(account.getBalance());
        java.math.BigDecimal newBalance = currentBalance.add(amountChange);
        if (newBalance.compareTo(java.math.BigDecimal.ZERO) < 0) return false;

        account.setBalance(newBalance.doubleValue());
        accountRepository.save(account);
        return true;
    }
}
