package com.nab.account_service.service;

import com.nab.account_service.model.Account;
import com.nab.account_service.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AccountService {
    
    @Autowired
    private AccountRepository accountRepository;
    
    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }
    
    public Optional<Account> getAccountById(Long id) {
        return accountRepository.findById(id);
    }
    
    public Account createAccount(Account account) {
        return accountRepository.save(account);
    }
    
    public Account updateAccount(Account account) {
        return accountRepository.save(account);
    }
    
    public void deleteAccount(Long id) {
        accountRepository.deleteById(id);
    }
}
