package org.example.sba.command;

import lombok.RequiredArgsConstructor;
import org.example.sba.model.Account;
import org.example.sba.repository.AccountRepository;
import org.example.sba.repository.RedisTokenRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ResetPasswordCommandHandler {
    private final AccountRepository accountRepository;
    private final RedisTokenRepository redisTokenRepository;
    private final PasswordEncoder passwordEncoder;

    public void handle(ResetPasswordCommand command) {
        Account account = accountRepository.findByUsername(command.getUsername())
                .orElseThrow(() -> new RuntimeException("Account not found"));

        // TODO: Validate reset token
        // if (!validateResetToken(command.getResetToken())) {
        //     throw new RuntimeException("Invalid reset token");
        // }

        account.setPassword(passwordEncoder.encode(command.getNewPassword()));
        accountRepository.save(account);
        redisTokenRepository.deleteById(command.getUsername());
    }
} 