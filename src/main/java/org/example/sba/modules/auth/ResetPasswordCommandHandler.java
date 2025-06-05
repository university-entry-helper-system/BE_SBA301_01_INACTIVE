package org.example.sba.modules.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.sba.model.Account;
import org.example.sba.repository.AccountRepository;
import org.example.sba.service.AccountService;
import org.example.sba.service.JwtService;
import org.example.sba.service.RedisTokenService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import static org.example.sba.util.TokenType.RESET_TOKEN;

@Slf4j
@Component
@RequiredArgsConstructor
public class ResetPasswordCommandHandler {
    private final AccountService accountService;
    private final JwtService jwtService;
    private final RedisTokenService redisTokenService;
    private final PasswordEncoder passwordEncoder;
    private final AccountRepository accountRepository;

    public String handle(ResetPasswordCommand command) {
        log.info("Processing password reset request");
        
        // Validate token
        String username = jwtService.extractUsername(command.getResetToken(), RESET_TOKEN);
        Account account = accountService.getByUsername(username);
        
        if (!jwtService.isValid(command.getResetToken(), RESET_TOKEN, account)) {
            log.warn("Invalid reset token for user: {}", username);
            throw new RuntimeException("Invalid or expired reset token");
        }

        // Validate new password
        if (!command.getNewPassword().equals(command.getConfirmPassword())) {
            throw new RuntimeException("Passwords do not match");
        }

        // Update password
        account.setPassword(passwordEncoder.encode(command.getNewPassword()));
        accountRepository.save(account);

        // Remove reset token
        redisTokenService.remove(username);

        log.info("Password reset successful for user: {}", username);
        return "Password has been reset successfully";
    }
} 