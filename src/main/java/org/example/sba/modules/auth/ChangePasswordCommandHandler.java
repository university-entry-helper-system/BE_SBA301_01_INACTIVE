package org.example.sba.modules.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.sba.model.Account;
import org.example.sba.repository.AccountRepository;
import org.example.sba.service.AccountService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChangePasswordCommandHandler {
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final AccountService accountService;

    public String handle(ChangePasswordCommand command) {
        log.info("Processing password change request for user: {}", command.getUsername());
        
        // Validate current password
        Account account = accountService.getByUsername(command.getUsername());
        if (!passwordEncoder.matches(command.getOldPassword(), account.getPassword())) {
            log.warn("Invalid current password for user: {}", command.getUsername());
            throw new RuntimeException("Current password is incorrect");
        }

        // Validate new password
        if (!command.getNewPassword().equals(command.getConfirmPassword())) {
            throw new RuntimeException("New passwords do not match");
        }

        // Update password
        account.setPassword(passwordEncoder.encode(command.getNewPassword()));
        accountRepository.save(account);

        log.info("Password changed successfully for user: {}", command.getUsername());
        return "Password changed successfully";
    }
} 