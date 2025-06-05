package org.example.sba.modules.account;

import lombok.RequiredArgsConstructor;
import org.example.sba.model.Account;
import org.example.sba.repository.AccountRepository;
import org.example.sba.service.AccountSyncService;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CreateAccountCommandHandler {
    private final AccountRepository accountRepository;
    private final AccountSyncService accountSyncService;

    public Account handle(CreateAccountCommand command) {
        Account account = Account.builder()
                .username(command.getUsername())
                .email(command.getEmail())
                .firstName(command.getFirstName())
                .lastName(command.getLastName())
                // ... set các trường khác
                .build();
        Account saved = accountRepository.save(account);
        accountSyncService.syncToMongo(saved);
        return saved;
    }
} 