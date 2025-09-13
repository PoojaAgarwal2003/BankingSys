package com.nab.account_service.dto;

import com.nab.account_service.enums.AccountStatus;
import com.nab.account_service.enums.KYCStatus;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AccountResponse {
    private Long id;
    private Long userId;
    private String accountNo;
    private Double balance;
    private AccountStatus accountStatus;
    private KYCStatus kycStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private UserResponse user;
}
