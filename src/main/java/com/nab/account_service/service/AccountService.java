package com.nab.account_service.service;

import com.nab.account_service.enums.AccountStatus;
import com.nab.account_service.enums.KYCStatus;
import com.nab.account_service.model.Account;
import com.nab.account_service.model.User;
import com.nab.account_service.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AccountService {
    
    private final AccountRepository accountRepository;
    
    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }
    
    public Optional<Account> getAccountById(Long id) {
        return accountRepository.findById(id);
    }
    
    @Transactional
    public Account createAccountForUser(User user) {
        Account account = new Account();
        account.setUserId(user.getId());
        account.setAccountNo(generateAccountNumber());
        account.setBalance(user.getCashDeposited());
        account.setAccountStatus(AccountStatus.APPROVED);
        account.setKycStatus(KYCStatus.PENDING); // Explicitly set KYC status to PENDING
        
        return accountRepository.save(account);
    }
    
    public Account updateAccount(Account account) {
        return accountRepository.save(account);
    }
    
    public void deleteAccount(Long id) {
        accountRepository.deleteById(id);
    }

    @Transactional
    public Account updateKycStatus(Long accountId, KYCStatus kycStatus) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        account.setKycStatus(kycStatus);
        return accountRepository.save(account);
    }

    private String generateAccountNumber() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 12; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }
}
