package org.example.sba.command;

import lombok.RequiredArgsConstructor;
import org.example.sba.dto.response.TokenResponse;
import org.example.sba.model.Account;
import org.example.sba.model.RedisToken;
import org.example.sba.model.Role;
import org.example.sba.repository.AccountRepository;
import org.example.sba.service.JwtService;
import org.example.sba.service.RedisTokenService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SignInCommandHandler {
    private final AuthenticationManager authenticationManager;
    private final AccountRepository accountRepository;
    private final JwtService jwtService;
    private final RedisTokenService redisTokenService;

    public TokenResponse handle(SignInCommand command) {
        var account = accountRepository.findByUsername(command.getUsername())
                .orElseThrow(() -> new RuntimeException("Account not found"));

        if (!account.isEnabled()) {
            throw new RuntimeException("Account not active");
        }

        List<Role> roles = accountRepository.findRolesByAccountId(account.getId());
        List<SimpleGrantedAuthority> authorities = roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .toList();

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                command.getUsername(), 
                command.getPassword(), 
                authorities));

        String accessToken = jwtService.generateToken(account);
        String refreshToken = jwtService.generateRefreshToken(account);

        redisTokenService.save(RedisToken.builder()
                .id(account.getUsername())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build());

        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .accountId(account.getId())
                .build();
    }
} 