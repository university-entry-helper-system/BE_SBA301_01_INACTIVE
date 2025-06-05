package org.example.sba.modules.account;

import lombok.RequiredArgsConstructor;
import org.example.sba.repository.AccountRepository;
import org.example.sba.service.AccountSyncService;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DeleteAccountCommandHandler {
    private final AccountRepository accountRepository;
    private final AccountSyncService accountSyncService;

    public void handle(DeleteAccountCommand command) {
        accountRepository.deleteById(command.getId());
        accountSyncService.deleteFromMongo(command.getId());
    }
} 