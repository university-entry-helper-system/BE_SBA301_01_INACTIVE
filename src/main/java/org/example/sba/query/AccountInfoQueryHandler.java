package org.example.sba.query;

import lombok.RequiredArgsConstructor;
import org.example.sba.model.Account;
import org.example.sba.repository.AccountRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AccountInfoQueryHandler {
    private final AccountRepository accountRepository;

    public Account handle(AccountInfoQuery query) {
        return accountRepository.findByUsername(query.getUsername())
                .orElseThrow(() -> new RuntimeException("Account not found"));
    }
} 