package org.example.sba.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.sba.model.Account;
import org.example.sba.model.AccountDocument;
import org.example.sba.repository.AccountMongoRepository;
import org.example.sba.service.AccountSyncService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountSyncServiceImpl implements AccountSyncService {
    private final AccountMongoRepository accountMongoRepository;

    @Override
    public void syncToMongo(Account account) {
        AccountDocument doc = new AccountDocument();
        doc.setId(account.getId().toString());
        doc.setUsername(account.getUsername());
        doc.setEmail(account.getEmail());
        doc.setFirstName(account.getFirstName());
        doc.setLastName(account.getLastName());
        // ... map các trường khác nếu cần
        accountMongoRepository.save(doc);
    }

    @Override
    public void deleteFromMongo(Long accountId) {
        accountMongoRepository.deleteById(accountId.toString());
    }
} 