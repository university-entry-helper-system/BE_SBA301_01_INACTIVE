package org.example.sba.service;

import org.example.sba.model.Account;

public interface AccountSyncService {
    void syncToMongo(Account account);
    void deleteFromMongo(Long accountId);
} 