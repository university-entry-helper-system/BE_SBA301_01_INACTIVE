package org.example.sba.modules.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.sba.model.Account;
import org.example.sba.service.AccountService;
import org.example.sba.service.JwtService;
import org.example.sba.service.RedisTokenService;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ForgotPasswordCommandHandler {
    private final AccountService accountService;
    private final JwtService jwtService;
    private final RedisTokenService redisTokenService;

    public String handle(ForgotPasswordCommand command) {
        log.info("Processing forgot password request for email: {}", command.getEmail());
        
        // Validate email format
        if (!command.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new RuntimeException("Invalid email format");
        }

        // Check if account exists
        Account account = accountService.getByEmail(command.getEmail());
        if (account == null) {
            log.warn("Forgot password request for non-existent email: {}", command.getEmail());
            // Don't reveal that email doesn't exist
            return "If your email is registered, you will receive a password reset link";
        }

        // Generate and send reset token
        String resetToken = jwtService.generateResetToken(account);
        redisTokenService.saveActivationToken(resetToken, account.getId(), 15 * 60); // 15 minutes

        log.info("Password reset token generated for user: {}", account.getUsername());
        return "If your email is registered, you will receive a password reset link";
    }
} 