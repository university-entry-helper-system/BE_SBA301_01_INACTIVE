package org.example.sba.command;

import lombok.RequiredArgsConstructor;
import org.example.sba.dto.response.TokenResponse;
import org.example.sba.model.Account;
import org.example.sba.model.RedisToken;
import org.example.sba.repository.AccountRepository;
import org.example.sba.service.JwtService;
import org.example.sba.service.RedisTokenService;
import org.springframework.stereotype.Component;

import static org.example.sba.util.TokenType.REFRESH_TOKEN;

@Component
@RequiredArgsConstructor
public class RefreshTokenCommandHandler {
    private final JwtService jwtService;
    private final AccountRepository accountRepository;
    private final RedisTokenService redisTokenService;

    public TokenResponse handle(RefreshTokenCommand command) {
        // Extract username from refresh token
        String username = jwtService.extractUsername(command.getRefreshToken(), REFRESH_TOKEN);
        
        // Get account
        Account account = accountRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        // Validate refresh token
        if (!jwtService.isValid(command.getRefreshToken(), REFRESH_TOKEN, account)) {
            throw new RuntimeException("Invalid refresh token");
        }

        // Generate new access token
        String accessToken = jwtService.generateToken(account);

        // Save new tokens to Redis
        redisTokenService.save(RedisToken.builder()
                .id(account.getUsername())
                .accessToken(accessToken)
                .refreshToken(command.getRefreshToken())
                .build());

        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(command.getRefreshToken())
                .accountId(account.getId())
                .build();
    }
} 