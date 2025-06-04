package org.example.sba.command;

import lombok.RequiredArgsConstructor;
import org.example.sba.model.Account;
import org.example.sba.repository.AccountRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChangePasswordCommandHandler {
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    public void handle(ChangePasswordCommand command) {
        Account account = accountRepository.findByUsername(command.getUsername())
                .orElseThrow(() -> new RuntimeException("Account not found"));

        if (!passwordEncoder.matches(command.getOldPassword(), account.getPassword())) {
            throw new RuntimeException("Old password is incorrect");
        }

        account.setPassword(passwordEncoder.encode(command.getNewPassword()));
        accountRepository.save(account);
    }
} 