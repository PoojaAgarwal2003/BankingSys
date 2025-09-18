package com.nab.account_service.kafka;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nab.account_service.enums.KYCStatus;
import com.nab.account_service.model.Account;
import com.nab.account_service.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KycStatusConsumer {

    @Autowired
    private AccountRepository accountRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @KafkaListener(topics = "kyc-status-updates", groupId = "account-service-group")
    public void listenKycStatusUpdate(String message) {
        try {
            JsonNode node = objectMapper.readTree(message);
            String userId = node.get("userId").asText();
            String kycStatus = node.get("kycStatus").asText();

            Account account = accountRepository.findFirstByUserId(userId);
            if (account != null) {
                account.setKycStatus(KYCStatus.valueOf(kycStatus));
                accountRepository.save(account);
            }
        } catch (Exception e) {
            // Log error
        }
    }
}