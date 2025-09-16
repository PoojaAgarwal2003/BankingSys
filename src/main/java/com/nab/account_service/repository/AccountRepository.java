package com.nab.account_service.repository;

import com.nab.account_service.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
	List<Account> findByUserId(String userId);
	Optional<Account> findByIdAndUserId(Long id, String userId);
	Optional<Account> findByAccountNo(String accountNo);
	
}
