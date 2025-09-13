package com.nab.account_service.model;

import com.nab.account_service.enums.AccountStatus;
import com.nab.account_service.enums.KYCStatus;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Data
@Entity
@Table(name = "accounts")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false, unique = true)
    private String accountNo;

    @Column(nullable = false)
    private Double balance;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountStatus accountStatus;

    @Enumerated(EnumType.STRING)
    private KYCStatus kycStatus;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private String email;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (balance == null) {
            balance = 0.0;
        }
        if (accountStatus == null) {
            accountStatus = AccountStatus.PENDING;
        }
        // Always set KYC status to PENDING for new accounts
        kycStatus = KYCStatus.PENDING;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
//this is comment